package ink.pmc.common.server.network

import ink.pmc.common.server.Server
import ink.pmc.common.server.gameserver.GameServer
import ink.pmc.common.server.message.Message

@Suppress("UNUSED")
interface Network {

    val proxy: Proxy
    val servers: Set<Server>
    val gameServers: Set<GameServer>
        get() = servers.filterIsInstance<GameServer>().toSet()

    fun broadcast(content: String): Message

    fun multicast(content: String, vararg receivers: Server) {
        multicast(content, receivers.toList())
    }

    fun multicast(content: String, receivers: Collection<Server>)

}