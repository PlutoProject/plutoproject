package ink.pmc.common.exchange.proxy

import com.velocitypowered.api.proxy.Player
import ink.pmc.common.exchange.*
import ink.pmc.common.exchange.utils.isInExchangeLobby

suspend fun exchange(player: Player) {
    if (!proxyExchangeService.isLobbyHealthy()) {
        player.sendMessage(EXCHANGE_LOBBY_OFFLINE)
        return
    }

    if (proxyExchangeService.isInExchange(player) || player.isInExchangeLobby) {
        player.sendMessage(EXCHANGE_START_FAILED_ALREADY_IN)
        return
    }

    proxyExchangeService.startExchange(player)
    player.sendMessage(EXCHANGE_START_SUCCEED)
}

suspend fun back(player: Player) {
    if (!proxyExchangeService.isLobbyHealthy()) {
        player.sendMessage(EXCHANGE_LOBBY_OFFLINE)
        return
    }

    if (!proxyExchangeService.isInExchange(player) || !player.isInExchangeLobby) {
        player.sendMessage(EXCHANGE_END_FAILED_NOT_IN)
        return
    }

    proxyExchangeService.endExchange(player)
    player.sendMessage(EXCHANGE_END_SUCCEED)
}