package ink.pmc.common.exchange.commands

import com.velocitypowered.api.proxy.Player
import ink.pmc.common.exchange.*
import ink.pmc.common.exchange.extensions.*
import ink.pmc.common.member.api.velocity.member
import ink.pmc.common.utils.chat.NON_PLAYER
import ink.pmc.common.utils.chat.PLAYER_NOT_ONLINE
import ink.pmc.common.utils.chat.replace
import ink.pmc.common.utils.command.VelocityCommand
import ink.pmc.common.utils.command.velocityRequiredOnlinePlayersArgument
import ink.pmc.common.utils.platform.proxy
import ink.pmc.common.utils.visual.mochaFlamingo
import ink.pmc.common.utils.visual.mochaText
import ink.pmc.common.utils.visual.mochaYellow
import net.kyori.adventure.text.Component
import org.incendo.cloud.kotlin.coroutines.extension.suspendingHandler
import org.incendo.cloud.parser.standard.LongParser

object TicketsCommand : VelocityCommand() {

    private val ticketsLookupSelf = velocityCommandManager.commandBuilder("tickets", "exchangetickets")
        .suspendingHandler {
            val sender = it.sender()

            if (sender !is Player) {
                sender.sendMessage(NON_PLAYER)
                return@suspendingHandler
            }

            val member = sender.member().sync()!!
            val tickets = member.exchangeTickets

            sender.sendMessage(
                TICKETS_LOOKUP_SELF
                    .replace("<amount>", Component.text(tickets).color(mochaText))
            )
        }

    private val ticketsLookupOther = velocityCommandManager.commandBuilder("tickets", "exchangetickets")
        .permission("exchange.tickets.lookup.other")
        .argument(velocityRequiredOnlinePlayersArgument())
        .suspendingHandler {
            val sender = it.sender()
            val name = it.get<String>("name")
            val player = proxy.getPlayer(name)

            if (proxy.getPlayer(name).isEmpty) {
                sender.sendMessage(PLAYER_NOT_ONLINE)
                return@suspendingHandler
            }

            val member = player.get().member().sync()!!
            val tickets = member.exchangeTickets

            sender.sendMessage(
                TICKETS_LOOKUP_OTHER
                    .replace("<player>", Component.text(player.get().username).color(mochaFlamingo))
                    .replace("<amount>", Component.text(tickets).color(mochaText))
            )
        }

    private val ticketsSet = velocityCommandManager.commandBuilder("tickets", "exchangetickets")
        .permission("exchange.tickets.set")
        .literal("set")
        .argument(velocityRequiredOnlinePlayersArgument())
        .required("amount", LongParser.longParser())
        .suspendingHandler {
            val sender = it.sender()
            val name = it.get<String>("name")
            val player = proxy.getPlayer(name)
            val amount = it.get<Long>("amount")

            if (proxy.getPlayer(name).isEmpty) {
                sender.sendMessage(PLAYER_NOT_ONLINE)
                return@suspendingHandler
            }

            val member = player.get().member().sync()!!
            member.tickets(amount)
            member.save()

            sender.sendMessage(
                TICKETS_SET_SUCCEED
                    .replace("<player>", Component.text(player.get().username).color(mochaYellow))
                    .replace("<amount>", Component.text(amount).color(mochaText))
            )
        }

    private val ticketsDeposit = velocityCommandManager.commandBuilder("tickets", "exchangetickets")
        .permission("exchange.tickets.deposit")
        .literal("deposit")
        .argument(velocityRequiredOnlinePlayersArgument())
        .required("amount", LongParser.longParser())
        .suspendingHandler {
            val sender = it.sender()
            val name = it.get<String>("name")
            val player = proxy.getPlayer(name)
            val amount = it.get<Long>("amount")

            if (proxy.getPlayer(name).isEmpty) {
                sender.sendMessage(PLAYER_NOT_ONLINE)
                return@suspendingHandler
            }

            val member = player.get().member().sync()!!
            member.deposit(amount)
            member.save()

            sender.sendMessage(
                TICKETS_DEPOSIT_SUCCEED
                    .replace("<player>", Component.text(player.get().username).color(mochaYellow))
                    .replace("<amount>", Component.text(amount).color(mochaText))
            )
        }

    private val ticketsWithdraw = velocityCommandManager.commandBuilder("tickets", "exchangetickets")
        .permission("exchange.tickets.withdraw")
        .literal("withdraw")
        .argument(velocityRequiredOnlinePlayersArgument())
        .required("amount", LongParser.longParser())
        .suspendingHandler {
            val sender = it.sender()
            val name = it.get<String>("name")
            val player = proxy.getPlayer(name)
            val amount = it.get<Long>("amount")

            if (proxy.getPlayer(name).isEmpty) {
                sender.sendMessage(PLAYER_NOT_ONLINE)
                return@suspendingHandler
            }

            val member = player.get().member().sync()!!

            if (!member.noLessThan(amount)) {
                sender.sendMessage(
                    TICKETS_WITHDRAW_FAILED_NOT_ENOUGH
                        .replace("<player>", Component.text(player.get().username).color(mochaYellow))
                )
                return@suspendingHandler
            }

            member.withdraw(amount)
            member.save()

            sender.sendMessage(
                TICKETS_WITHDRAW_SUCCEED
                    .replace("<player>", Component.text(player.get().username).color(mochaYellow))
                    .replace("<amount>", Component.text(amount).color(mochaText))
            )
        }

    init {
        command(ticketsLookupSelf)
        command(ticketsLookupOther)
        command(ticketsSet)
        command(ticketsDeposit)
        command(ticketsWithdraw)
    }

}