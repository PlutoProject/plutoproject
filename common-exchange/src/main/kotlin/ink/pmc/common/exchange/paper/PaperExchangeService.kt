package ink.pmc.common.exchange.paper

import ink.pmc.common.exchange.*
import ink.pmc.common.exchange.paper.utils.*
import ink.pmc.common.member.api.paper.member
import ink.pmc.common.utils.chat.replace
import ink.pmc.common.utils.platform.threadSafeTeleport
import ink.pmc.common.utils.visual.mochaText
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import java.util.*

class PaperExchangeService(override val lobby: ExchangeLobby) : AbstractPaperExchangeService() {

    override val inExchange: MutableList<UUID> = mutableListOf()

    override suspend fun startExchange(player: Player) {
        if (isInExchange(player)) {
            player.sendMessage(EXCHANGE_START_FAILED_ALREADY_IN)
            return
        }

        snapshotStatus(player)
        player.threadSafeTeleport(exchangeLobby.teleportLocation)
        applyExchangeStatus(player)
        hidePlayer(player)
        inExchange.add(player.uniqueId)
        player.sendMessage(EXCHANGE_START_SUCCEED)
        player.member.update()
    }

    override suspend fun endExchange(player: Player, goBack: Boolean) {
        if (!isInExchange(player)) {
            player.sendMessage(EXCHANGE_END_FAILED_NOT_IN)
            return
        }

        clearInventory(player)
        restoreStatus(player, goBack)
        showPlayer(player)
        inExchange.remove(player.uniqueId)
        player.sendMessage(EXCHANGE_END_SUCCEED)
        player.member.update()
    }

    override suspend fun checkout(player: Player): Long {
        if (!isInExchange(player)) {
            return 0
        }

        val member = player.member.refresh()!!
        val cart = cart(player)
        val price = cart.size.toLong()

        if (!noLessThan(player.member, price)) {
            player.sendMessage(
                CHECKOUT_FAILED_TICKETS_NOT_ENOUGH
                    .replace("<amount>", Component.text(price).color(mochaText))
            )
            return 0
        }

        if (!withdraw(member, price)) {
            player.sendMessage(CHECKOUT_FAILED_UNKNOWN_ISSUE)
            return 0
        }

        clearInventory(player)
        restoreStatus(player)
        showPlayer(player)
        inExchange.remove(player.uniqueId)
        player.sendMessage(
            CHECKOUT_SUCCEED
                .replace("<amount>", Component.text(price).color(mochaText))
        )
        distributeItems(player, cart)
        player.member.update()

        return price
    }

    override fun isInExchange(player: Player): Boolean {
        return exchangeLobby.players.contains(player) && inExchange.contains(player.uniqueId)
    }

}