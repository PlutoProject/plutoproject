package ink.pmc.exchange.utils

import com.velocitypowered.api.proxy.Player
import ink.pmc.exchange.lobbyServerName


val Player.isInExchangeLobby: Boolean
    get() {
        if (this.currentServer.isEmpty) {
            return false
        }

        val server = this.currentServer.get().serverInfo.name
        return server == lobbyServerName
    }