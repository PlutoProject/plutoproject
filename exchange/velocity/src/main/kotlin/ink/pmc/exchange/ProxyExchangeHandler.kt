package ink.pmc.exchange

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent

@Suppress("UNUSED")
object ProxyExchangeHandler {

    @Subscribe
    fun disconnectEvent(event: DisconnectEvent) {
        val player = event.player
        proxyExchangeService.inExchange.remove(player)
    }

}