package ink.pmc.utils.command

import org.incendo.cloud.Command
import org.incendo.cloud.CommandManager

@Suppress("UNUSED")
fun <C> CommandManager<C>.init(command: ink.pmc.utils.command.Command<C>) {
    command.commands.forEach {
        this.command(it)
    }
}

@Suppress("UNUSED")
abstract class Command<C> {

    val commands: MutableList<Command.Builder<C>> = mutableListOf()

    fun command(builder: Command.Builder<C>) {
        commands.add(builder)
    }
}