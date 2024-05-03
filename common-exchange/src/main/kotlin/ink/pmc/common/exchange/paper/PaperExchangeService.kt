package ink.pmc.common.exchange.paper

import ink.pmc.common.exchange.ExchangeLobby
import ink.pmc.common.exchange.exchangeLobby
import ink.pmc.common.exchange.paper.utils.clearInventory
import ink.pmc.common.exchange.paper.utils.restoreStatus
import ink.pmc.common.exchange.paper.utils.snapshotStatus
import ink.pmc.common.utils.platform.threadSafeTeleport
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

class PaperExchangeService(override val lobby: ExchangeLobby) : AbstractPaperExchangeService() {

    override val inExchange: MutableList<UUID> = CopyOnWriteArrayList()
    override val statusSnapshots: MutableMap<UUID, StatusSnapshot> = ConcurrentHashMap()

    override fun startExchange(player: Player) {
        if (isInExchange(player)) {
            return
        }

        player.threadSafeTeleport(exchangeLobby.teleportLocation)
        inExchange.add(player.uniqueId)
        snapshotStatus(player)
        clearInventory(player)
    }

    override fun endExchange(player: Player, goBack: Boolean) {
        if (!isInExchange(player)) {
            return
        }

        inExchange.remove(player.uniqueId)
        clearInventory(player)
        restoreStatus(player, goBack)
    }

    override fun isInExchange(player: Player): Boolean {
        return exchangeLobby.players.contains(player) && inExchange.contains(player.uniqueId)
    }

}