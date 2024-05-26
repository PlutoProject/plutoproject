package ink.pmc.exchange.backend

import ink.pmc.exchange.DAILY_TICKETS
import ink.pmc.exchange.TICKETS_LIMIT
import ink.pmc.exchange.TICKETS_RANDOM_GOT
import ink.pmc.exchange.exchangeService
import ink.pmc.member.api.Member
import ink.pmc.utils.concurrent.submitAsyncIO
import kotlinx.coroutines.flow.MutableStateFlow
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Enemy
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import kotlin.random.Random

private const val BLOCK_BREAKS_REQUIREMENT = 5
private const val BLOCK_PLACE_REQUIREMENT = 10
private const val ENEMY_KILLS_REQUIREMENT = 50
private const val PLAYER_CONSUME_REQUIREMENT = 10

private const val BLOCK_BREAK_PRODUCE_BASE_CHANCE = 0.010255
private const val BLOCK_PLACE_PRODUCE_BASE_CHANCE = 0.013425
private const val BASE_ENEMY_KILLS_PRODUCE_CHANGE = 0.30
private const val BASE_PLAYER_CONSUME_PRODUCE_CHANGE = 0.05

private const val POINT_LIMIT = 5.0

private const val BASE_CALC_CHANCE = 0.5
private const val BASE_CHANCE_DECREASE_FACTOR = BASE_CALC_CHANCE / TICKETS_LIMIT

private const val COMMON_PRODUCE_MIN = 0.5
private const val COMMON_PRODUCE_MAX = 1.5
private const val COMMON_DECREASE_MIN = 0.0
private const val COMMON_DECREASE_MAX = 1.0
private const val ENEMY_PRODUCE_MIN = 0.0
private const val ENEMY_PRODUCE_MAX = 1.0
private const val PLAYER_DAMAGE_DECREASE_MIN = 1.0
private const val PLAYER_DAMAGE_DECREASE_MAX = 2.5
private const val PLAYER_DEATH_RESET_CHANCE = 0.25

@Suppress("UNUSED")
class RandomTicketsHandler(player: Player, private val member: Member) : Listener {

    private val uuid = player.uniqueId
    private val player
        get() = Bukkit.getPlayer(uuid)
    private val blockBreaks = MutableStateFlow(0)
    private val blockPlaces = MutableStateFlow(0)
    private val enemiesKilled = MutableStateFlow(0)
    private val itemConsumed = MutableStateFlow(0)
    private val points = MutableStateFlow(0.0)

    private fun reset() {
        blockPlaces.value = 0
        blockBreaks.value = 0
        enemiesKilled.value = 0
        itemConsumed.value = 0
        points.value = 0.0
    }

    private fun produce(baseChance: Double, min: Double = COMMON_PRODUCE_MIN, max: Double = COMMON_PRODUCE_MAX) {
        val tickets = exchangeService.tickets(member)

        if (tickets >= TICKETS_LIMIT) {
            return
        }

        val chance = when (tickets >= DAILY_TICKETS) {
            true -> {
                val shouldBeCalc = tickets - DAILY_TICKETS
                val factorMultiply = baseChance / BASE_CALC_CHANCE
                val decreasePerTicket = BASE_CHANCE_DECREASE_FACTOR * factorMultiply
                val decrease = decreasePerTicket * shouldBeCalc
                baseChance - decrease
            }

            false -> {
                baseChance
            }
        }

        val random = Math.random()
        if (random >= chance) {
            return
        }

        val value = Random.nextDouble(min, max)

        points.value += when (points.value + value > POINT_LIMIT) {
            true -> {
                POINT_LIMIT - points.value
            }

            false -> {
                value
            }
        }

        if (points.value < POINT_LIMIT) {
            return
        }

        exchangeService.deposit(member, 1)
        submitAsyncIO { member.save() }
        points.value = 0.0

        player?.sendMessage(TICKETS_RANDOM_GOT)
    }

    private fun decrease(chance: Double, min: Double = COMMON_DECREASE_MIN, max: Double = COMMON_DECREASE_MAX) {
        if (points.value <= 0) {
            return
        }

        if (Math.random() >= chance) {
            return
        }

        val decrease = Random.nextDouble(min, max)
        points.value -= when (points.value < decrease) {
            true -> {
                points.value
            }

            false -> {
                decrease
            }
        }
    }

    @EventHandler
    fun blockBreakEvent(event: BlockBreakEvent) {
        if (event.player != player) {
            return
        }

        blockBreaks.value += 1

        if (blockBreaks.value < BLOCK_BREAKS_REQUIREMENT) {
            return
        }

        blockBreaks.value = 0
        produce(BLOCK_BREAK_PRODUCE_BASE_CHANCE)
    }

    @EventHandler
    fun playerItemConsumeEvent(event: PlayerItemConsumeEvent) {
        if (event.player != player) {
            return
        }

        itemConsumed.value += 1

        if (itemConsumed.value < PLAYER_CONSUME_REQUIREMENT) {
            return
        }

        itemConsumed.value = 0
        produce(BASE_PLAYER_CONSUME_PRODUCE_CHANGE)
    }

    @EventHandler
    fun blockPlaceEvent(event: BlockPlaceEvent) {
        if (event.player != player) {
            return
        }

        blockPlaces.value += 1

        if (blockPlaces.value < BLOCK_PLACE_REQUIREMENT) {
            return
        }

        blockPlaces.value = 0
        produce(BLOCK_PLACE_PRODUCE_BASE_CHANCE)
    }


    @EventHandler
    fun entityDamageByEntityEvent(event: EntityDamageByEntityEvent) {
        val entity = event.entity

        if (entity !is Enemy) {
            return
        }

        if (entity.health != 0.0) {
            return
        }

        if (event.damager !is Player) {
            return
        }

        val damager = event.damager

        if (damager != player) {
            return
        }

        enemiesKilled.value += 1

        if (enemiesKilled.value < ENEMY_KILLS_REQUIREMENT) {
            return
        }

        enemiesKilled.value = 0
        produce(BASE_ENEMY_KILLS_PRODUCE_CHANGE, ENEMY_PRODUCE_MIN, ENEMY_PRODUCE_MAX)
    }

    @EventHandler
    fun entityDamageEvent(event: EntityDamageEvent) {
        val entity = event.entity

        if (event !is Player) {
            return
        }

        if (entity != player) {
            return
        }

        val maxHealth = player?.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value ?: return
        val baseDecreaseChance = 1 / maxHealth
        val chance = baseDecreaseChance * event.damage
        decrease(chance, PLAYER_DAMAGE_DECREASE_MIN, PLAYER_DAMAGE_DECREASE_MAX)
    }

    @EventHandler
    fun playerDeathEvent(event: PlayerDeathEvent) {
        if (event.player != player) {
            return
        }

        if (Math.random() >= PLAYER_DEATH_RESET_CHANCE) {
            return
        }

        reset()
    }

}