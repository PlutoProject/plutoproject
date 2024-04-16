package ink.pmc.common.misc.commands

import ink.pmc.common.misc.SUICIDE
import ink.pmc.common.misc.commandManager
import ink.pmc.common.utils.chat.NON_PLAYER
import ink.pmc.common.utils.command.PaperCommand
import ink.pmc.common.utils.concurrent.sync
import org.bukkit.entity.Player
import org.incendo.cloud.kotlin.coroutines.extension.suspendingHandler

object SuicideCommand : PaperCommand() {

    private val suicide = commandManager.commandBuilder("suicide")
        .suspendingHandler {
            val sender = it.sender()

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