package ink.pmc.exchange.commands

import ink.pmc.exchange.*
import ink.pmc.exchange.utils.isCheckoutSign
import ink.pmc.exchange.utils.markAsCheckoutSign
import ink.pmc.framework.utils.chat.NON_PLAYER
import ink.pmc.framework.utils.command.PaperCommand
import ink.pmc.framework.utils.concurrent.sync
import org.bukkit.entity.Player
import org.incendo.cloud.kotlin.coroutines.extension.suspendingHandler

object PaperExchangeAdminCommand : PaperCommand() {

    private val exchangeAdminMarkAsCheckoutSign = paperCommandManager.commandBuilder("exchangeadmin")
        .permission(EXCHANGE_ADMIN_PERMISSION)
        .literal("marksign")
        .suspendingHandler {
            val sender = it.sender().sender

            if (sender !is Player) {
                sender.sendMessage(NON_PLAYER)
                return@suspendingHandler
            }

            val block = sender.getTargetBlockExact(4) ?: run {
                sender.sendMessage(EXCHANGE_ADMIN_MARK_AS_CHECKOUT_SIGN_NOT_FOUNT_IN_RANGE)
                return@suspendingHandler
            }

            if (!block.type.toString().lowercase().contains("sign")) {
                sender.sendMessage(EXCHANGE_ADMIN_MARK_AS_CHECKOUT_SIGN_FAILED_NOT_SIGN)
                return@suspendingHandler
            }

            block.location.sync {
                if (isCheckoutSign(block)) {
                    sender.sendMessage(EXCHANGE_ADMIN_MARK_AS_CHECKOUT_SIGN_FAILED_ALREADY_IS)
                    return@sync
                }

                markAsCheckoutSign(block)
                sender.sendMessage(EXCHANGE_ADMIN_MARK_AS_CHECKOUT_SIGN_SUCCEED)
            }
        }

    init {
        command(exchangeAdminMarkAsCheckoutSign)
    }

}