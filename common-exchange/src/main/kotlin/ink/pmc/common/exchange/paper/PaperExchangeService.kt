package ink.pmc.common.exchange.paper

import ink.pmc.common.exchange.EXCHANGE_TICKET_KEY
import ink.pmc.common.exchange.ExchangeLobby
import ink.pmc.common.exchange.exchangeLobby
import ink.pmc.common.exchange.paper.utils.clearInventory
import ink.pmc.common.exchange.paper.utils.restoreStatus
import ink.pmc.common.exchange.paper.utils.snapshotStatus
import ink.pmc.common.member.api.Member
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

    override fun tickets(member: Member): Long {
        return member.dataContainer.getLong(EXCHANGE_TICKET_KEY) ?: 0
    }

    override fun deposit(member: Member, amount: Long): Boolean {
        return try {
            member.dataContainer[EXCHANGE_TICKET_KEY] = tickets(member) + amount
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun withdraw(member: Member, amount: Long): Boolean {
        return try {
            if (!noLessThan(member, amount)) {
                return false
            }
            member.dataContainer[EXCHANGE_TICKET_KEY] = tickets(member) - amount
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun match(member: Member, condition: (Long) -> Boolean): Boolean {
        return condition.invoke(tickets(member))
    }

}