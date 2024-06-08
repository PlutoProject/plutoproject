package ink.pmc.exchange.proxy

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.player.ServerConnectedEvent
import ink.pmc.member.api.Member
import ink.pmc.member.api.velocity.member
import ink.pmc.member.api.velocity.player
import ink.pmc.utils.chat.replace
import ink.pmc.utils.concurrent.submitAsync
import ink.pmc.utils.platform.proxy
import ink.pmc.utils.visual.mochaPink
import ink.pmc.exchange.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import net.kyori.adventure.text.Component
import java.time.*

@Suppress("UNUSED")
class TicketDistributor {

    private var backgroundDist: Job

    init {
        backgroundDist = startDistJob()
    }

    private fun startDistJob(): Job {
        return submitAsync {
            while (true) {
                val now = LocalDateTime.now()
                val tomorrowMidnight = now.toLocalDate().plusDays(1).atStartOfDay()
                val durationUntilMidnight = Duration.between(now, tomorrowMidnight).toMillis()
                val bufferMillis = 1000L
                
                delay(durationUntilMidnight)
                distAll()
                serverLogger.info("Distributed tickets to all online player")
                delay(bufferMillis)
            }
        }
    }

    private suspend fun distAll() {
        proxy.allPlayers.forEach {
            distributeTicket(it.member())
        }
    }

    suspend fun stopDistJob() {
        backgroundDist.cancelAndJoin()
    }

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
        val localInstant = zonedInstant.toLocalDate().plusDays(1).atStartOfDay()
        val localNow = zonedNow.toLocalDateTime()

        return localNow.isAfter(localInstant)
    }

    private fun markAsTicketed(member: Member) {
        member.dataContainer[LAST_TICKET_DISTRIBUTE_KEY] = Instant.now().toEpochMilli()
    }

    private suspend fun distributeTicket(member: Member) {
        if (!shouldDistributeTicket(member)) {
            return
        }

        if (exchangeService.tickets(member) > DAILY_TICKETS) {
            return
        }

        exchangeService.tickets(member, DAILY_TICKETS)
        markAsTicketed(member)
        member.player!!.sendMessage(
            TICKETS_DISTRIBUTE_SUCCEED.replace(
                "<amount>", Component.text(DAILY_TICKETS).color(
                    mochaPink
                )
            )
        )

        member.save()
    }

}