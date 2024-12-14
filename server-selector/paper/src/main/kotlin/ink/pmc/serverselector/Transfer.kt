package ink.pmc.serverselector

import ink.pmc.advkt.component.text
import ink.pmc.advkt.showTitle
import ink.pmc.advkt.title.*
import ink.pmc.essentials.TELEPORT_FAILED_SOUND
import ink.pmc.essentials.TELEPORT_SUCCEED_SOUND
import ink.pmc.essentials.TELEPORT_SUCCEED_TITLE
import ink.pmc.framework.bridge.Bridge
import ink.pmc.framework.bridge.player.toBridge
import ink.pmc.framework.bridge.server.ServerState
import ink.pmc.framework.bridge.server.ServerType
import ink.pmc.framework.utils.inject.koin
import ink.pmc.framework.utils.visual.mochaMaroon
import ink.pmc.framework.utils.visual.mochaText
import ink.pmc.serverselector.storage.UserRepository
import net.kyori.adventure.util.Ticks
import org.bukkit.entity.Player

private val userRepo by koin.inject<UserRepository>()

suspend fun Player.transferServer(id: String) {
    val bridgePlayer = toBridge()
    runCatching {
        bridgePlayer.switchServer(id)
    }.onFailure {
        showTitle {
            times {
                fadeIn(Ticks.duration(5))
                stay(Ticks.duration(35))
                fadeOut(Ticks.duration(20))
            }
            mainTitle {
                text("传送失败") with mochaMaroon
            }
            subTitle {
                text("请再试一次") with mochaText
            }
        }
        playSound(TELEPORT_FAILED_SOUND)
        return
    }
    val remotePlayer = Bridge.getPlayer(uniqueId, ServerState.REMOTE, ServerType.BACKEND) ?: error("Unexpected")
    remotePlayer.showTitle(TELEPORT_SUCCEED_TITLE)
    remotePlayer.playSound(TELEPORT_SUCCEED_SOUND)
    val userModel = userRepo.findOrCreate(uniqueId)
    userRepo.saveOrUpdate(userModel.copy(previouslyJoinedServer = id))
}