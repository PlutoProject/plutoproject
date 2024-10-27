package ink.pmc.misc.commands

import ink.pmc.framework.utils.chat.NON_PLAYER
import ink.pmc.framework.utils.concurrent.sync
import ink.pmc.misc.SUICIDE
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Command

@Suppress("UNUSED")
object SuicideCommand {
    @Command("suicide")
    suspend fun suicide(sender: CommandSender) {
        if (sender !is Player) {
            sender.sendMessage(NON_PLAYER)
            return
        }
        sync {
            sender.health = 0.0
            sender.sendMessage(SUICIDE)
        }
    }
}