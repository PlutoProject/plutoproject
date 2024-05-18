package ink.pmc.common.exchange.lobby.commands

import ink.pmc.common.exchange.lobby.checkout
import ink.pmc.common.exchange.paperCommandManager
import ink.pmc.common.utils.chat.NON_PLAYER
import ink.pmc.common.utils.command.PaperCommand
import org.bukkit.entity.Player
import org.incendo.cloud.kotlin.coroutines.extension.suspendingHandler
import org.incendo.cloud.kotlin.extension.commandBuilder

@Suppress("UNUSED")
object LobbyCheckoutCommand : PaperCommand() {

    private val checkout = paperCommandManager.commandBuilder("checkout", aliases = arrayOf("buy")) {
        suspendingHandler {
            val sender = it.sender()

            if (sender !is Player) {
                sender.sendMessage(NON_PLAYER)
                return@suspendingHandler
            }

            checkout(sender)
        }
    }.commandBuilder

    init {
        command(checkout)
    }

}