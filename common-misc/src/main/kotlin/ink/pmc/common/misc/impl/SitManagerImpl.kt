package ink.pmc.common.misc.impl

import ink.pmc.common.misc.*
import ink.pmc.common.misc.api.isSitting
import ink.pmc.common.misc.api.sit.SitManager
import ink.pmc.common.misc.api.stand
import ink.pmc.common.utils.execute
import ink.pmc.common.utils.regionScheduler
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import java.util.*
import kotlin.time.Duration

@OptIn(DelicateCoroutinesApi::class)
class SitManagerImpl : SitManager {

    private val _sitter = mutableMapOf<UUID, Location>()
    private val armorStands = mutableMapOf<UUID, UUID>()

    override val sitters: Map<UUID, Location> = _sitter

    init {
        GlobalScope.launch {
            while (!disabled) {
                _sitter.keys.forEach {
                    val player = plugin.server.getPlayer(it)
                    player!!.sendActionBar(STAND_UP)

                    // 避免异步实体获取问题
                    regionScheduler(plugin, player.location) {
                        val playerId = player.uniqueId
                        val armorStandId = armorStands[playerId]!!
                        val armorStand = player.world.getEntity(armorStandId)
                            ?: return@regionScheduler // 有时可能玩家已经站起来了，但是异步任务仍然尝试获取实体

                        // 切换到实体调度器执行，因为不允许异步操作实体数据
                        armorStand.execute(plugin) {
                            // 避免一些意外问题
                            if (!armorStand.passengers.contains(player)) {
                                armorStand.addPassenger(player)
                            }
                        }
                    }
                }
                delay(Duration.parse("2s"))
            }
        }
    }

    override fun sit(player: Player, location: Location) {
        if (player.isSitting) {
            player.stand()
        }

        var sitLoc = location.toBlockLocation()

        if (!checkLocation(location)) {
            val tryFind = findLegalLocation(location)

            if (tryFind == null) {
                player.sendMessage(ILLEGAL_LOC)
                return
            }

            sitLoc = tryFind
        }

        // Location#toBlockLocation 不会抹掉 pitch 与 yaw
        sitLoc.pitch = 0F
        sitLoc.yaw = 0F

        val armorStand = createArmorStand(sitLoc)
        markAsSeat(armorStand, player)
        armorStand.addPassenger(player)

        _sitter[player.uniqueId] = sitLoc
        armorStands[player.uniqueId] = armorStand.uniqueId
        markDelay(player)
        player.sendActionBar(STAND_UP) // 先直接发一次，尝试覆盖原版的载具提示
    }

    override fun isSitting(player: Player): Boolean {
        return _sitter.containsKey(player.uniqueId)
    }

    override fun stand(player: Player) {
        if (!isSitting(player)) {
            return
        }

        val playerId = player.uniqueId
        val armorStandId = armorStands[playerId]!!
        val armorStand = player.world.getEntity(armorStandId)!!
        val standLocation = player.location.add(0.0, 1.0, 0.0)

        // 使用实体调度器，避免未来在迁移 Folia 时可能造成的问题
        armorStand.execute(plugin) {
            armorStand.removePassenger(player)
        }

        player.teleportAsync(standLocation) // 显式异步传送，同上
        player.sendActionBar(Component.text(" "))

        _sitter.remove(playerId)
        cleanArmorStand(armorStandId)
    }

    override fun getSeat(player: Player): Entity? {
        armorStands[player.uniqueId] ?: return null
        return plugin.server.getEntity(armorStands[player.uniqueId]!!)
    }

    override fun getSitLocation(player: Player): Location? {
        return sitters[player.uniqueId]
    }

    override fun standAll() {
        _sitter.keys.forEach {
            val player = plugin.server.getPlayer(it)
            player!!.stand()
        }
    }

    private fun cleanArmorStand(uuid: UUID) {
        if (!armorStands.containsValue(uuid)) {
            return
        }

        val armorStand = plugin.server.getEntity(uuid) ?: return
        armorStand.remove()

        armorStands.remove(uuid)
    }

}