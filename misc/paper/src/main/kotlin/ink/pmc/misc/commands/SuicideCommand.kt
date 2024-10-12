package ink.pmc.misc.commands

import ink.pmc.misc.SUICIDE
import ink.pmc.misc.commandManager
import ink.pmc.utils.chat.NON_PLAYER
import ink.pmc.utils.command.PaperCommand
import ink.pmc.utils.concurrent.sync
import ink.pmc.utils.dsl.cloud.sender
import org.bukkit.entity.Player
import org.incendo.cloud.kotlin.coroutines.extension.suspendingHandler

object SuicideCommand : PaperCommand() {

    private val suicide = commandManager.commandBuilder("suicide")
        .suspendingHandler {
            val sender = it.sender

            if (sender !is Player) {
                sender.sendMessage(NON_PLAYER)
                return@suspendingHandler
            }

            sync {
                sender.health = 0.0
                sender.sendMessage(SUICIDE)
            }
        }

    init {
        command(suicide)
    }

}