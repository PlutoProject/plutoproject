package ink.pmc.common.member

import com.velocitypowered.api.proxy.Player

fun Player.startPlay() {
    PlayTimeLogger.start(this)
}

fun Player.stopPlay(): Long {
    return PlayTimeLogger.stop(this)
}