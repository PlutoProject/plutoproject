package ink.pmc.common.utils.command

import org.bukkit.command.CommandSender
import org.incendo.cloud.CommandManager

@Suppress("UNUSED")
fun CommandManager<CommandSender>.init(command: Command<CommandSender>) {
    command.commands.forEach {
        this.command(it)
    }
}

@Suppress("UNUSED")
abstract class PaperCommand : Command<CommandSender>()