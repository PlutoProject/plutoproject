package ink.pmc.common.exchange.commands

import ink.pmc.common.exchange.paperCommandManager
import ink.pmc.common.exchange.backendExchangeService
import ink.pmc.common.utils.chat.NON_PLAYER
import ink.pmc.common.utils.command.PaperCommand
import ink.pmc.common.utils.concurrent.sync
import org.bukkit.entity.Player
import org.incendo.cloud.kotlin.coroutines.extension.suspendingHandler

object CheckoutCommand : PaperCommand() {

    private val checkout = paperCommandManager.commandBuilder("checkout")
        .suspendingHandler {
            val sender = it.sender()

            if (sender !is Player) {
                sender.sendMessage(NON_PLAYER)
                return@suspendingHandler
            }

            if (!backendExchangeService.isInExchange(sender)) {
                return@suspendingHandler
            }

            sender.sync { backendExchangeService.checkout(sender) }
        }

    init {
        command(checkout)
    }

}