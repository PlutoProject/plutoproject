package ink.pmc.essentials.commands

import ink.pmc.utils.chat.NON_PLAYER
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

suspend fun checkPlayer(sender: CommandSender, action: suspend Player.() -> Unit) {
    if (sender !is Player) {
        sender.sendMessage(NON_PLAYER)
        return
    }
    sender.action()
}