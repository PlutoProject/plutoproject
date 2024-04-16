package ink.pmc.common.misc.commands

import ink.pmc.common.misc.commandManager
import ink.pmc.common.misc.sitManager
import ink.pmc.common.utils.chat.NON_PLAYER
import ink.pmc.common.utils.command.PaperCommand
import ink.pmc.common.utils.concurrent.sync
import org.bukkit.entity.Player
import org.incendo.cloud.kotlin.coroutines.extension.suspendingHandler

object SitCommand : PaperCommand() {

    private val sit = commandManager.commandBuilder("sit")
        .suspendingHandler {
            val sender = it.sender()

            if (sender !is Player) {
                sender.sendMessage(NON_PLAYER)
                return@suspendingHandler
            }

            sync {
                sitManager.sit(sender, sender.location)
            }
        }

    init {
        command(sit)
    }

}