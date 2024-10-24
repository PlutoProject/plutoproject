package ink.pmc.framework.utils.command

import com.velocitypowered.api.command.CommandSource
import org.incendo.cloud.CommandManager

@Suppress("UNUSED")
fun CommandManager<CommandSource>.init(command: Command<CommandSource>) {
    command.commands.forEach {
        this.command(it)
    }
}

@Suppress("UNUSED")
abstract class VelocityCommand : Command<CommandSource>()