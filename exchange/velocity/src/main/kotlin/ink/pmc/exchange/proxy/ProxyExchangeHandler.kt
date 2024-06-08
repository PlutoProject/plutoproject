package ink.pmc.exchange.proxy

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import ink.pmc.exchange.proxyExchangeService

@Suppress("UNUSED")
object ProxyExchangeHandler {

    @Subscribe
    fun disconnectEvent(event: DisconnectEvent) {
        val player = event.player
        proxyExchangeService.inExchange.remove(player)
    }

}