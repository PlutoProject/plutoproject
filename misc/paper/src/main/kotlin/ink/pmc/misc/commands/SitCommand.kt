package ink.pmc.misc.commands

import ink.pmc.framework.utils.chat.NON_PLAYER
import ink.pmc.framework.utils.concurrent.sync
import ink.pmc.misc.api.sit.SitManager
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Command

@Suppress("UNUSED")
object SitCommand {
    @Command("sit")
    suspend fun sit(sender: CommandSender) {
        if (sender !is Player) {
            sender.sendMessage(NON_PLAYER)
            return
        }
        sync {
            SitManager.sit(sender, sender.location)
        }
    }
}