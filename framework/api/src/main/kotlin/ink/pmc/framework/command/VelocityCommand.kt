package ink.pmc.framework.command

import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.proxy.Player
import ink.pmc.framework.chat.NON_PLAYER

inline fun ensurePlayer(sender: CommandSource, action: Player.() -> Unit) {
    if (sender !is Player) {
        sender.sendMessage(NON_PLAYER)
        return
    }
    sender.action()
}

@JvmName("ensurePlayerReceiver")
inline fun CommandSource.ensurePlayer(action: Player.() -> Unit) {
    ensurePlayer(this, action)
}