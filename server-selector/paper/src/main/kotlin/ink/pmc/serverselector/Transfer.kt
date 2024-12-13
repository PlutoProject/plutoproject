package ink.pmc.serverselector

import ink.pmc.advkt.component.text
import ink.pmc.advkt.showTitle
import ink.pmc.advkt.sound.key
import ink.pmc.advkt.sound.sound
import ink.pmc.advkt.title.*
import ink.pmc.framework.bridge.Bridge
import ink.pmc.framework.bridge.player.toBridge
import ink.pmc.framework.bridge.server.ServerState
import ink.pmc.framework.bridge.server.ServerType
import ink.pmc.framework.utils.visual.mochaGreen
import ink.pmc.framework.utils.visual.mochaMaroon
import ink.pmc.framework.utils.visual.mochaText
import kotlinx.coroutines.withTimeoutOrNull
import net.kyori.adventure.key.Key
import net.kyori.adventure.util.Ticks
import org.bukkit.entity.Player
import kotlin.time.Duration.Companion.seconds

private val FAILED_SOUND = sound {
    key(Key.key("block.amethyst_cluster.break"))
}

private val SUCCEED_SOUND = sound {
    key(Key.key("entity.enderman.teleport"))
}

private val SUCCEED_PROMPT = arrayOf(
    "落地！",
    "到站了~",
    "请带好随身行李",
    "开门请当心",
    "下车请注意安全"
)

suspend fun Player.transferServer(id: String) {
    val bridgePlayer = toBridge()
    withTimeoutOrNull(10.seconds) {
        bridgePlayer.switchServer(id)
        val remotePlayer = Bridge.getPlayer(uniqueId, ServerState.REMOTE, ServerType.BACKEND) ?: error("Unexpected")
        remotePlayer.showTitle {
            times {
                fadeIn(Ticks.duration(5))
                stay(Ticks.duration(35))
                fadeOut(Ticks.duration(20))
            }
            mainTitle {
                text(SUCCEED_PROMPT.random()) with mochaGreen
            }
            subTitle {
                text("已传送至目标位置") with mochaText
            }
        }
        remotePlayer.playSound(SUCCEED_SOUND)
    } ?: run {
        showTitle {
            times {
                fadeIn(Ticks.duration(5))
                stay(Ticks.duration(35))
                fadeOut(Ticks.duration(20))
            }
            mainTitle {
                text("传送超时") with mochaMaroon
            }
            subTitle {
                text("请再试一次") with mochaText
            }
        }
        playSound(FAILED_SOUND)
    }
    if (!isOnline) return
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
    playSound(FAILED_SOUND)
}