package ink.pmc.exchange.lobby

import ink.pmc.exchange.*
import ink.pmc.exchange.proto.lobby2proxy.itemDistributeNotify
import ink.pmc.exchange.proto.proxy2server.ExchangeEndAckOuterClass
import ink.pmc.exchange.proto.server2lobby.exchangeEnd
import ink.pmc.exchange.utils.cart
import ink.pmc.exchange.utils.clearInventory
import ink.pmc.member.api.paper.member
import ink.pmc.utils.chat.replace
import ink.pmc.utils.player.itemStackArrayToBase64
import ink.pmc.utils.proto.player.player
import ink.pmc.utils.visual.mochaText
import ink.pmc.utils.visual.mochaYellow
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

suspend fun checkout(player: Player) {
    val member = player.member()
    val cart = cart(player)
    val price = cart.size.toLong()

    when (price == 0L) {
        true -> {
            player.sendMessage(CHECKOUT_SUCCEED_EMPTY)
        }

        false -> {
            if (!exchangeService.noLessThan(member, price)) {
                player.sendMessage(
                    CHECKOUT_FAILED_TICKETS_NOT_ENOUGH.replace(
                        "<amount>", Component.text(price).color(mochaYellow)
                    )
                )
                return
            }

            exchangeService.withdraw(member, price)
            member.save()

            player.sendMessage(CHECKOUT_SUCCEED.replace("<amount>", Component.text(price).color(mochaText)))
        }
    }

    clearInventory(player)

    val ack = lobbyExchangeService.stub.endExchange(exchangeEnd {
        serviceId = lobbyExchangeService.id.toString()
        this.player = player {
            username = player.name
            uuid = player.uniqueId.toString()
        }
    })

    when (ack.result) {
        ExchangeEndAckOuterClass.ExchangeEndResult.END_FAILED_UNKOWN -> {
            player.sendMessage(EXCHANGE_END_FAILED_UNKNOWN)
            serverLogger.severe("Failed to send player back: ${player.name}")
            return
        }

        else -> {}
    }

    if (cart.isEmpty()) {
        return
    }

    lobbyExchangeService.stub.notifyItemDistribute(itemDistributeNotify {
        serviceId = lobbyExchangeService.id.toString()
        this.player = player {
            username = player.name
            uuid = player.uniqueId.toString()
        }
        cost = price
        items = itemStackArrayToBase64(cart.toTypedArray())
    })
}