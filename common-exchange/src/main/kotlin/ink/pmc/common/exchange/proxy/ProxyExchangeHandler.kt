package ink.pmc.common.exchange.proxy

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import ink.pmc.common.exchange.proxyExchangeService

@Suppress("UNUSED")
object ProxyExchangeHandler {

    @Subscribe
    fun disconnectEvent(event: DisconnectEvent) {
        val player = event.player
        proxyExchangeService.inExchange.remove(player)
    }

}