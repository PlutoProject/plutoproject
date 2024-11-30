package ink.pmc.framework.utils.chat

import com.velocitypowered.api.proxy.ProxyServer
import ink.pmc.framework.utils.platform.proxy
import net.kyori.adventure.text.Component

fun ProxyServer.broadcast(message: Component) {
    proxy.consoleCommandSource.sendMessage(message)
    proxy.allPlayers.forEach { it.sendMessage(message) }
}