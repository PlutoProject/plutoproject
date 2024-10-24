package ink.pmc.transfer.backend.lobby

import com.electronwill.nightconfig.core.Config
import ink.pmc.member.api.Member
import ink.pmc.member.api.MemberService
import ink.pmc.member.api.paper.memberOrNull
import ink.pmc.transfer.AbstractTransferService
import ink.pmc.transfer.LOBBY_EVENT_BYPASS_PERMISSION
import ink.pmc.transfer.MEMBER_PLAYED_ONCE_DATA_KEY
import ink.pmc.transfer.api.Destination
import ink.pmc.transfer.backend.lobby.portal.PortalBounding.HandlerType
import ink.pmc.transfer.backend.lobby.portal.PortalManager
import ink.pmc.transfer.scripting.LobbyConfigureScopeImpl
import ink.pmc.transfer.scripting.evalLobbyConfigureScript
import ink.pmc.transfer.serverLogger
import ink.pmc.framework.utils.concurrent.io
import ink.pmc.framework.utils.concurrent.submitAsync
import ink.pmc.framework.utils.concurrent.submitAsyncIO
import ink.pmc.framework.utils.concurrent.sync
import ink.pmc.framework.utils.multiplaform.player.paper.wrapped
import ink.pmc.framework.utils.platform.threadSafeTeleport
import kotlinx.coroutines.delay
import net.kyori.adventure.text.Component
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import kotlin.script.experimental.api.SourceCode
import kotlin.time.Duration.Companion.seconds

@Suppress("UNUSED")
class TransferLobby(private val service: AbstractTransferService, config: Config, private val source: SourceCode) {

    private val worldName = config.get<String>("world")
    private val menu: TransferMenu
    private val conditionCache = mutableMapOf<Player, MutableList<ConditionCache>>()
    val world = loadWorld(worldName).apply { initWorldEnvironment(this) }
    private val spawnPoint = Location(
        world,
        config.get("spawn-point.x"),
        config.get("spawn-point.y"),
        config.get("spawn-point.z"),
        config.get("spawn-point.yaw"),
        config.get("spawn-point.pitch")
    )
    val portalManager = PortalManager(config.get("portal"), this)
    val listener = LobbyListener(this)

    init {
        val scope = evalScript()
        menu = TransferMenu(
            service,
            this,
            scope.main ?: throw IllegalStateException("Main menu not configured!"),
            scope.categoryMenus
        )
        portalManager.bounding.addHandler(HandlerType.ENTER) {
            handleMenuOpen(it)
        }
        portalManager.bounding.addHandler(HandlerType.EXIT) {
            handleMenuClose(it)
        }
    }

    private suspend fun handleMenuOpen(player: Player) {
        val view = portalManager.getView(player) ?: return
        view.off()
        delay(100)
        menu.openWindow(player)
    }

    fun handleMenuClose(player: Player) {
        val view = portalManager.getView(player) ?: return
        view.on()
    }

    private fun evalScript(): LobbyConfigureScopeImpl {
        serverLogger.info("Evaluating lobby configure script...")
        val scope = LobbyConfigureScopeImpl()
        evalLobbyConfigureScript(source, scope)
        serverLogger.info("Done!")
        return scope
    }

    private fun initWorldEnvironment(world: World) {
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false)
        world.setGameRule(GameRule.DO_MOB_LOOT, false)
        world.setGameRule(GameRule.MOB_GRIEFING, false)
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false)
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false)
        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false)
        world.time = 1000
    }

    private fun loadWorld(name: String): World {
        return Bukkit.createWorld(WorldCreator.name(name))
            ?: throw IllegalStateException("Failed to load transfer world!")
    }

    suspend fun verifyCondition(player: Player, destination: Destination): Pair<Boolean, Component?> {
        val cachedValue = conditionCache[player]?.firstOrNull { it.destination == destination.id }?.value
        return cachedValue ?: service.conditionManager.verifyCondition(player.wrapped, destination)
    }

    suspend fun initConditionCache(player: Player) {
        val list = conditionCache.computeIfAbsent(player) { mutableListOf() }
        service.destinations.forEach {
            list.add(
                ConditionCache(
                    it.id,
                    service.conditionManager.verifyCondition(player.wrapped, it)
                )
            )
        }
    }

    fun tick() {
        portalManager.tick()
    }

    data class ConditionCache(val destination: String, val value: Pair<Boolean, Component?>)

    class LobbyListener(private val lobby: TransferLobby) : Listener {

        @EventHandler
        suspend fun playerJoinEvent(event: PlayerJoinEvent) {
            event.joinMessage(null)

            val player = event.player.apply {
                gameMode = GameMode.ADVENTURE
                clearActivePotionEffects()
                threadSafeTeleport(lobby.spawnPoint)
            }
            val member = player.memberOrNull()
            val portalView = lobby.portalManager.createView(player)

            val title = if (!MemberService.isWhitelisted(player.uniqueId)) {
                PLAYER_JOIN_NOT_WHITELISTED
            } else if (isFirstJoin(member!!)) {
                setJoined(member)
                portalView.on()
                PLAYER_JOIN_WHITELISTED_FIRST
            } else {
                portalView.on()
                PLAYER_JOIN_WHITELISTED
            }

            player.showTitle(title)
            delay(200)
            portalView.on()
            lobby.initConditionCache(player)
        }

        private fun isFirstJoin(member: Member): Boolean {
            return !member.dataContainer.getBoolean(MEMBER_PLAYED_ONCE_DATA_KEY)
        }

        private fun setJoined(member: Member) {
            member.dataContainer[MEMBER_PLAYED_ONCE_DATA_KEY] = true
            submitAsyncIO { member.save() }
        }

        @EventHandler
        fun playerQuitEvent(event: PlayerQuitEvent) {
            val player = event.player
            lobby.portalManager.destroyView(player)
            event.quitMessage(null)
        }

        @EventHandler
        fun playerInteractEvent(event: PlayerInteractEvent) {
            val player = event.player

            if (player.hasPermission(LOBBY_EVENT_BYPASS_PERMISSION)) {
                return
            }

            event.isCancelled = true
        }

    }

    suspend fun transferPlayer(player: Player, destination: Destination) {
        player.sync {
            player.gameMode = GameMode.SPECTATOR
            player.threadSafeTeleport(spawnPoint)
            player.showTitle(LOBBY_TRANSFER_PREPARE_TITLE)
            player.addPotionEffect(
                PotionEffect(
                    PotionEffectType.SLOWNESS,
                    1000,
                    3,
                    false,
                    false
                )
            )

            delay(500L)

            val job = submitAsync {
                delay(5.seconds)
                if (!player.isOnline) {
                    return@submitAsync
                }
                player.sync {
                    player.threadSafeTeleport(spawnPoint)
                    player.gameMode = GameMode.ADVENTURE
                    player.clearActivePotionEffects()
                    player.showTitle(LOBBY_TRANSFER_FAILED_TITLE)
                }
            }

            io { destination.transfer(player.wrapped) }
            job.cancel()
        }
    }

    fun destroy() {
        portalManager.destroyAll()
    }

}