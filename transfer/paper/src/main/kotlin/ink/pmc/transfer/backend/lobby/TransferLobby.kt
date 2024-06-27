package ink.pmc.transfer.backend.lobby

import com.electronwill.nightconfig.core.Config
import ink.pmc.member.api.Member
import ink.pmc.member.api.MemberService
import ink.pmc.member.api.paper.memberOrNull
import ink.pmc.transfer.backend.lobby.portal.PortalManager
import ink.pmc.utils.concurrent.submitAsyncIO
import org.bukkit.Bukkit
import org.bukkit.GameRule
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

@Suppress("UNUSED")
class TransferLobby(config: Config) {

    private val worldName = config.get<String>("world")
    private val world = loadWorld(worldName).apply { initWorldEnvironment(this) }
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

            val player = event.player
            val member = player.memberOrNull()

            val title = if (!MemberService.isWhitelisted(player.uniqueId)) {
                PLAYER_JOIN_NOT_WHITELISTED
            } else if (isFirstJoin(member!!)) {
                setJoined(member)
                PLAYER_JOIN_WHITELISTED_FIRST
            } else {
                PLAYER_JOIN_WHITELISTED
            }

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

    }

}