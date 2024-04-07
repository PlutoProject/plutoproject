package ink.pmc.common.member.velocity

import com.velocitypowered.api.proxy.Player
import ink.pmc.common.member.PlayTimeLogger

fun Player.startPlay() {
    PlayTimeLogger.start(this)
}

fun Player.stopPlay(): Long {
    return PlayTimeLogger.stop(this)
}