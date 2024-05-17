package ink.pmc.common.exchange.paper

import ink.pmc.common.exchange.*
import ink.pmc.common.exchange.utils.*
import ink.pmc.common.member.api.paper.member
import ink.pmc.common.utils.chat.replace
import ink.pmc.common.utils.concurrent.submitAsyncIO
import ink.pmc.common.utils.platform.threadSafeTeleport
import ink.pmc.common.utils.visual.mochaText
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

class BackendExchangeService : AbstractBackendExchangeService() {

    override suspend fun startExchange(player: Player) {
        if (isInExchange(player)) {
            player.sendMessage(EXCHANGE_START_FAILED_ALREADY_IN)
            return
        }

        // inExchange.add(player.uniqueId)
        player.member()
        snapshotStatus(player)
        player.threadSafeTeleport(exchangeLobby.teleportLocation)
        applyExchangeStatus(player)
        hidePlayer(player)
        player.sendMessage(EXCHANGE_START_SUCCEED)
        submitAsyncIO { player.member().save() }
    }

    override suspend fun endExchange(player: Player) {
        if (!isInExchange(player)) {
            player.sendMessage(EXCHANGE_END_FAILED_NOT_IN)
            return
        }

        // inExchange.remove(player.uniqueId)
        player.member()
        clearInventory(player)
        // restoreStatus(player, goBack)
        showPlayer(player)
        player.sendMessage(EXCHANGE_END_SUCCEED)
        submitAsyncIO { player.member().save() }
    }

    suspend fun checkout(player: Player): Long {
        if (!isInExchange(player)) {
            return 0
        }

        val member = player.member()
        val cart = cart(player)
        val price = cart.size.toLong()

        if (!noLessThan(player.member(), price)) {
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

        // inExchange.remove(player.uniqueId)
        clearInventory(player)
        restoreStatus(player)
        showPlayer(player)
        player.sendMessage(
            CHECKOUT_SUCCEED
                .replace("<amount>", Component.text(price).color(mochaText))
        )
        distributeItems(player, cart)
        player.member().save()

        return price
    }

    override suspend fun isInExchange(player: Player): Boolean {
        return exchangeLobby.players.contains(player) // inExchange.contains(player.uniqueId)
    }

}