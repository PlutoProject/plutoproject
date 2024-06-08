package ink.pmc.exchange.proxy.commands

import com.velocitypowered.api.proxy.Player
import ink.pmc.exchange.*
import ink.pmc.exchange.proxy.tickets
import ink.pmc.member.api.velocity.member
import ink.pmc.utils.chat.NON_PLAYER
import ink.pmc.utils.chat.PLAYER_NOT_ONLINE
import ink.pmc.utils.chat.replace
import ink.pmc.utils.command.VelocityCommand
import ink.pmc.utils.command.velocityRequiredOnlinePlayersArgument
import ink.pmc.utils.platform.proxy
import ink.pmc.utils.visual.mochaText
import ink.pmc.utils.visual.mochaYellow
import net.kyori.adventure.text.Component
import org.incendo.cloud.kotlin.coroutines.extension.suspendingHandler
import org.incendo.cloud.kotlin.extension.commandBuilder
import org.incendo.cloud.parser.standard.LongParser

object ProxyTicketsCommand : VelocityCommand() {

    private val tickets = velocityCommandManager.commandBuilder("tickets") {
        suspendingHandler {
            val sender = it.sender()

            if (sender !is Player) {
                sender.sendMessage(NON_PLAYER)
                return@suspendingHandler
            }

            tickets(sender, sender)
        }
    }.commandBuilder

    private val ticketsOther = velocityCommandManager.commandBuilder("tickets") {
        permission(EXCHANGE_ADMIN_PERMISSION)
        argument(velocityRequiredOnlinePlayersArgument())

        suspendingHandler {
            val sender = it.sender()
            val target = it.get<String>("name")

            if (proxy.getPlayer(target).isEmpty) {
                sender.sendMessage(PLAYER_NOT_ONLINE)
                return@suspendingHandler
            }

            val player = proxy.getPlayer(target).get()
            tickets(player, sender)
        }
    }.commandBuilder

    private val ticketsSet = velocityCommandManager.commandBuilder("tickets") {
        permission(EXCHANGE_ADMIN_PERMISSION)
        argument(velocityRequiredOnlinePlayersArgument())
        literal("set")
        required("amount", LongParser.longParser())

        suspendingHandler {
            val sender = it.sender()
            val target = it.get<String>("name")
            val amount = it.get<Long>("amount")

            if (proxy.getPlayer(target).isEmpty) {
                sender.sendMessage(PLAYER_NOT_ONLINE)
                return@suspendingHandler
            }

            val player = proxy.getPlayer(target).get()
            val member = player.member()

            exchangeService.tickets(member, amount)
            member.save()

            sender.sendMessage(
                TICKETS_SET_SUCCEED
                    .replace("<player>", Component.text(player.username).color(mochaYellow))
                    .replace("<amount>", Component.text(amount).color(mochaText))
            )
        }
    }.commandBuilder

    private val ticketsDeposit = velocityCommandManager.commandBuilder("tickets") {
        permission(EXCHANGE_ADMIN_PERMISSION)
        argument(velocityRequiredOnlinePlayersArgument())
        literal("deposit")
        required("amount", LongParser.longParser())

        suspendingHandler {
            val sender = it.sender()
            val target = it.get<String>("name")
            val amount = it.get<Long>("amount")

            if (proxy.getPlayer(target).isEmpty) {
                sender.sendMessage(PLAYER_NOT_ONLINE)
                return@suspendingHandler
            }

            val player = proxy.getPlayer(target).get()
            val member = player.member()

            exchangeService.deposit(member, amount)
            member.save()

            sender.sendMessage(
                TICKETS_DEPOSIT_SUCCEED
                    .replace("<player>", Component.text(player.username).color(mochaYellow))
                    .replace("<amount>", Component.text(amount).color(mochaText))
            )
        }
    }.commandBuilder

    private val ticketsWithdraw = velocityCommandManager.commandBuilder("tickets") {
        permission(EXCHANGE_ADMIN_PERMISSION)
        argument(velocityRequiredOnlinePlayersArgument())
        literal("withdraw")
        required("amount", LongParser.longParser())

        suspendingHandler {
            val sender = it.sender()
            val target = it.get<String>("name")
            val amount = it.get<Long>("amount")

            if (proxy.getPlayer(target).isEmpty) {
                sender.sendMessage(PLAYER_NOT_ONLINE)
                return@suspendingHandler
            }

            val player = proxy.getPlayer(target).get()
            val member = player.member()

            if (!exchangeService.noLessThan(member, amount)) {
                sender.sendMessage(
                    TICKETS_WITHDRAW_FAILED_NOT_ENOUGH
                        .replace("<player>", Component.text(player.username).color(mochaYellow))
                )
                return@suspendingHandler
            }

            exchangeService.withdraw(member, amount)
            member.save()

            sender.sendMessage(
                TICKETS_WITHDRAW_SUCCEED
                    .replace("<player>", Component.text(player.username).color(mochaYellow))
                    .replace("<amount>", Component.text(amount).color(mochaText))
            )
        }
    }.commandBuilder

    init {
        command(tickets)
        command(ticketsOther)
        command(ticketsSet)
        command(ticketsDeposit)
        command(ticketsWithdraw)
    }

}