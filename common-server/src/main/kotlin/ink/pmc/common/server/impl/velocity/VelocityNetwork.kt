package ink.pmc.common.server.impl.velocity

import ink.pmc.common.server.Server
import ink.pmc.common.server.message.Message
import ink.pmc.common.server.network.Network
import ink.pmc.common.server.network.Proxy
import ink.pmc.common.server.player.ServerPlayer
import java.util.*

class VelocityNetwork(override val proxy: Proxy) : Network {

    override val servers: MutableSet<Server> = mutableSetOf()

    override fun broadcast(content: String): Message {
        TODO("Not yet implemented")
    }

    override fun multicast(content: String, receivers: Collection<Server>) {
        TODO("Not yet implemented")
    }

    override fun isServerRegistered(uuid: UUID): Boolean {
        TODO("Not yet implemented")
    }

    override fun getServer(uuid: UUID): Server? {
        TODO("Not yet implemented")
    }

    override fun isOnline(uuid: UUID): Boolean {
        TODO("Not yet implemented")
    }

    override fun getPlayer(uuid: UUID): ServerPlayer? {
        TODO("Not yet implemented")
    }

}