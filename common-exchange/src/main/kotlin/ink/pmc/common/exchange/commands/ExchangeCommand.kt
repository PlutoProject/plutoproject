package ink.pmc.common.exchange.commands

import ink.pmc.common.exchange.*
import ink.pmc.common.exchange.utils.isCheckoutSign
import ink.pmc.common.exchange.utils.markAsCheckoutSign
import ink.pmc.common.utils.chat.NON_PLAYER
import ink.pmc.common.utils.chat.PLAYER_NOT_ONLINE
import ink.pmc.common.utils.chat.replace
import ink.pmc.common.utils.command.PaperCommand
import ink.pmc.common.utils.command.paperRequiredOnlinePlayersArgument
import ink.pmc.common.utils.concurrent.sync
import ink.pmc.common.utils.visual.mochaYellow
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.block.data.type.WallSign
import org.bukkit.entity.Player
import org.incendo.cloud.kotlin.coroutines.extension.suspendingHandler

object ExchangeCommand : PaperCommand() {

    private val exchange = paperCommandManager.commandBuilder("exchange")
        .suspendingHandler {
            val sender = it.sender()

            if (sender !is Player) {
                sender.sendMessage(NON_PLAYER)
                return@suspendingHandler
            }

            if (paperExchangeService.isInExchange(sender)) {
                return@suspendingHandler
            }

            sender.sync { paperExchangeService.startExchange(sender) }
        }

    private val exchangeAdminStart = paperCommandManager.commandBuilder("exchangeadmin")
        .permission(EXCHANGE_ADMIN_PERMISSION)
        .literal("start")
        .argument(paperRequiredOnlinePlayersArgument())
        .suspendingHandler {
            val sender = it.sender()
            val player = Bukkit.getPlayer(it.get<String>("name"))

            if (player == null) {
                sender.sendMessage(PLAYER_NOT_ONLINE)
                return@suspendingHandler
            }

            if (paperExchangeService.isInExchange(player)) {
                sender.sendMessage(
                    EXCHANGE_ADMIN_START_FAILED_ALREADY_IN
                        .replace("<player>", Component.text(player.name).color(mochaYellow))
                )
                return@suspendingHandler
            }

            player.sync { paperExchangeService.startExchange(player) }
            sender.sendMessage(
                EXCHANGE_ADMIN_START_SUCCEED
                    .replace("<player>", Component.text(player.name).color(mochaYellow))
            )
        }

    private val exchangeAdminEnd = paperCommandManager.commandBuilder("exchangeadmin")
        .permission(EXCHANGE_ADMIN_PERMISSION)
        .literal("end")
        .argument(paperRequiredOnlinePlayersArgument())
        .suspendingHandler {
            val sender = it.sender()
            val player = Bukkit.getPlayer(it.get<String>("name"))

            if (player == null) {
                sender.sendMessage(PLAYER_NOT_ONLINE)
                return@suspendingHandler
            }

            if (!paperExchangeService.isInExchange(player)) {
                sender.sendMessage(
                    EXCHANGE_ADMIN_END_FAILED_NOT_IN
                        .replace("<player>", Component.text(player.name).color(mochaYellow))
                )
                return@suspendingHandler
            }

            player.sync { paperExchangeService.endExchange(player) }
            sender.sendMessage(
                EXCHANGE_ADMIN_END_SUCCEED
                    .replace("<player>", Component.text(player.name).color(mochaYellow))
            )
        }

    private val exchangeAdminCheckout = paperCommandManager.commandBuilder("exchangeadmin")
        .permission(EXCHANGE_ADMIN_PERMISSION)
        .literal("checkout")
        .argument(paperRequiredOnlinePlayersArgument())
        .suspendingHandler {
            val sender = it.sender()
            val player = Bukkit.getPlayer(it.get<String>("name"))

            if (player == null) {
                sender.sendMessage(PLAYER_NOT_ONLINE)
                return@suspendingHandler
            }

            if (!paperExchangeService.isInExchange(player)) {
                sender.sendMessage(
                    EXCHANGE_ADMIN_END_FAILED_NOT_IN
                        .replace("<player>", Component.text(player.name).color(mochaYellow))
                )
                return@suspendingHandler
            }

            player.sync { paperExchangeService.checkout(player) }
            sender.sendMessage(
                EXCHANGE_ADMIN_CHECKOUT_SUCCEED
                    .replace("<player>", Component.text(player.name).color(mochaYellow))
            )
        }

    private val exchangeAdminMarkAsCheckoutSign = paperCommandManager.commandBuilder("exchangeadmin")
        .permission(EXCHANGE_ADMIN_PERMISSION)
        .literal("marksign")
        .suspendingHandler {
            val sender = it.sender()

            if (sender !is Player) {
                sender.sendMessage(NON_PLAYER)
                return@suspendingHandler
            }

            if (!paperExchangeService.lobby.players.contains(sender)) {
                sender.sendMessage(EXCHANGE_ADMIN_MARK_AS_CHECKOUT_SIGN_FAILED_NOT_IN_EXCHANGE)
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
        command(exchange)
        command(exchangeAdminStart)
        command(exchangeAdminEnd)
        command(exchangeAdminCheckout)
        command(exchangeAdminMarkAsCheckoutSign)
    }

}