package ink.pmc.framework.utils.command

import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.command.CommandSender
import org.incendo.cloud.CommandManager

@Suppress("UNUSED")
fun CommandManager<CommandSender>.init(command: Command<CommandSender>) {
    command.commands.forEach {
        this.command(it)
    }
}

@Suppress("UNUSED")
abstract class PaperCommand : Command<CommandSourceStack>()