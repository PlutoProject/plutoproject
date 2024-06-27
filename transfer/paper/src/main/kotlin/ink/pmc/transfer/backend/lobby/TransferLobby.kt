package ink.pmc.transfer.backend.lobby

import com.electronwill.nightconfig.core.Config
import ink.pmc.member.api.Member
import ink.pmc.member.api.MemberService
import ink.pmc.member.api.paper.memberOrNull
import ink.pmc.transfer.LOBBY_EVENT_BYPASS_PERMISSION
import ink.pmc.transfer.MEMBER_PLAYED_ONCE_DATA_KEY
import ink.pmc.transfer.backend.lobby.portal.PortalManager
import ink.pmc.utils.concurrent.submitAsyncIO
import ink.pmc.utils.platform.threadSafeTeleport
import org.bukkit.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

@Suppress("UNUSED")
class TransferLobby(config: Config) {

    private val worldName = config.get<String>("world")
    private val world = loadWorld(worldName).apply { initWorldEnvironment(this) }
    private val spawnPoint = Location(
        world,
        config.get("spawn-point.x"),
        config.get("spawn-point.y"),
        config.get("spawn-point.z"),
        config.get("spawn-point.yaw"),
        config.get("spawn-point.pitch")
    )
    val portalManager = PortalManager(config.get("portal"), world)
    val listener = LobbyListener(this)

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

    class LobbyListener(private val lobby: TransferLobby) : Listener {

        @EventHandler
        suspend fun playerJoinEvent(event: PlayerJoinEvent) {
            event.joinMessage(null)

            val player = event.player.apply { threadSafeTeleport(lobby.spawnPoint) }
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

            portalView.update()
            player.showTitle(title)
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

}