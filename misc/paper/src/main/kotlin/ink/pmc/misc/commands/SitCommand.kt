package ink.pmc.misc.commands

import ink.pmc.misc.commandManager
import ink.pmc.misc.sitManager
import ink.pmc.utils.chat.NON_PLAYER
import ink.pmc.utils.command.PaperCommand
import ink.pmc.utils.concurrent.sync
import org.bukkit.entity.Player
import org.incendo.cloud.kotlin.coroutines.extension.suspendingHandler

object SitCommand : PaperCommand() {

    private val sit = commandManager.commandBuilder("sit")
        .suspendingHandler {
            val sender = it.sender().sender

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