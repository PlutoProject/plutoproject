package ink.pmc.framework.command

import ink.pmc.framework.chat.NON_PLAYER
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

inline fun ensurePlayer(sender: CommandSender, action: Player.() -> Unit) {
    if (sender !is Player) {
        sender.sendMessage(NON_PLAYER)
        return
    }
    sender.action()
}

@JvmName("ensurePlayerReceiver")
inline fun CommandSender.ensurePlayer(action: Player.() -> Unit) {
    ensurePlayer(this, action)
}

inline fun <reified T : OfflinePlayer> selectPlayer(self: CommandSender, other: T?): T? {
    return other ?: if (self is T) self else null
}