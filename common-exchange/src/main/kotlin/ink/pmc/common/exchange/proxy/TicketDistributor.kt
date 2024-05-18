package ink.pmc.common.exchange.proxy

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.player.ServerConnectedEvent
import ink.pmc.common.exchange.LAST_TICKET_DISTRIBUTE_KEY
import ink.pmc.common.exchange.TICKETS_DISTRIBUTE_SUCCEED
import ink.pmc.common.exchange.dailyTickets
import ink.pmc.common.exchange.exchangeService
import ink.pmc.common.member.api.Member
import ink.pmc.common.member.api.velocity.member
import ink.pmc.common.member.api.velocity.player
import ink.pmc.common.utils.chat.replace
import ink.pmc.common.utils.visual.mochaPink
import net.kyori.adventure.text.Component
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit

@Suppress("UNUSED")
object TicketDistributor {

    @Subscribe
    suspend fun serverConnectedEvent(event: ServerConnectedEvent) {
        val player = event.player
        val member = player.member()
        distributeTicket(member)
    }

    private fun shouldDistributeTicket(member: Member): Boolean {
        val data = member.dataContainer

        if (!data.contains(LAST_TICKET_DISTRIBUTE_KEY)) {
            return true
        }

        val timestamp = data.getLong(LAST_TICKET_DISTRIBUTE_KEY)!!
        val instant = Instant.ofEpochMilli(timestamp)
        val now = Instant.now()

        if (instant.isAfter(Instant.now())) {
            return false
        }

        val id = "Asia/Shanghai"
        val zonedInstant = instant.atZone(ZoneId.of(id))
        val zonedNow = now.atZone(ZoneId.of(id))
        val between = ChronoUnit.DAYS.between(zonedInstant, zonedNow)

        println(between)

        return between > 0
    }

    private fun markAsTicketed(member: Member) {
        member.dataContainer[LAST_TICKET_DISTRIBUTE_KEY] = System.currentTimeMillis()
    }

    private suspend fun distributeTicket(member: Member) {
        if (!shouldDistributeTicket(member)) {
            return
        }

        exchangeService.tickets(member, dailyTickets)
        markAsTicketed(member)
        member.player!!.sendMessage(
            TICKETS_DISTRIBUTE_SUCCEED.replace(
                "<amount>", Component.text(dailyTickets).color(
                    mochaPink
                )
            )
        )
        member.save()
    }

}