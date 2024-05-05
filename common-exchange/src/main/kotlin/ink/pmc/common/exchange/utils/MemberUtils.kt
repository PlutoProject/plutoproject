package ink.pmc.common.exchange.utils

import ink.pmc.common.exchange.ExchangeConfig
import ink.pmc.common.exchange.LAST_TICKET_DISTRIBUTE_KEY
import ink.pmc.common.exchange.extensions.tickets
import ink.pmc.common.member.api.Member
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit

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

    return between > 0
}

private fun markAsTicketed(member: Member) {
    member.dataContainer[LAST_TICKET_DISTRIBUTE_KEY] = System.currentTimeMillis()
}

fun distributeTicket(member: Member) {
    if (!shouldDistributeTicket(member)) {
        return
    }

    member.tickets(ExchangeConfig.Tickets.daily)
    markAsTicketed(member)
}