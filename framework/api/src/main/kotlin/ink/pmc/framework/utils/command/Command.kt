package ink.pmc.framework.utils.command

import ink.pmc.framework.utils.chat.NON_PLAYER
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.Command
import org.incendo.cloud.CommandManager

@Suppress("UNUSED")
fun <C> CommandManager<C>.init(command: ink.pmc.framework.utils.command.Command<C>) {
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

suspend fun checkPlayer(sender: CommandSender, action: suspend Player.() -> Unit) {
    if (sender !is Player) {
        sender.sendMessage(NON_PLAYER)
        return
    }
    sender.action()
}