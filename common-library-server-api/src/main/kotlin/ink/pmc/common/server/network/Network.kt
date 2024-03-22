package ink.pmc.common.server.network

import ink.pmc.common.server.Server
import ink.pmc.common.server.gameserver.GameServer
import ink.pmc.common.server.message.Message
import ink.pmc.common.server.player.ServerPlayer
import java.util.*

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

    fun isServerRegistered(uuid: UUID): Boolean

    fun getServer(uuid: UUID): Server?

    fun isOnline(uuid: UUID): Boolean

    fun getPlayer(uuid: UUID): ServerPlayer?

    fun transferServer(player: ServerPlayer, server: Server) {
        proxy.transferServer(player, server)
    }

}