package ink.pmc.framework.bridge

import ink.pmc.framework.bridge.player.BridgePlayer
import ink.pmc.framework.bridge.server.BridgeGroup
import ink.pmc.framework.bridge.server.BridgeServer
import java.util.*

class ProxyBridge : Bridge {
    override val self: BridgeServer
        get() = TODO("Not yet implemented")
    override val master: BridgeServer
        get() = TODO("Not yet implemented")
    override val groups: Collection<BridgeGroup>
        get() = TODO("Not yet implemented")
    override val players: Collection<BridgePlayer>
        get() = TODO("Not yet implemented")
    override val playerCount: Int
        get() = TODO("Not yet implemented")

    override fun getGroup(id: String): BridgeGroup? {
        TODO("Not yet implemented")
    }

    override fun isGroupRegistered(id: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun getPlayer(name: String): BridgePlayer? {
        TODO("Not yet implemented")
    }

    override fun getPlayer(uniqueId: UUID): BridgePlayer? {
        TODO("Not yet implemented")
    }

    override fun isPlayerOnline(name: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun isPlayerOnline(uniqueId: UUID): Boolean {
        TODO("Not yet implemented")
    }

    override val servers: Collection<BridgeServer>
        get() = TODO("Not yet implemented")

    override fun getServer(id: String): BridgeServer? {
        TODO("Not yet implemented")
    }

    override fun isServerRegistered(id: String): Boolean {
        TODO("Not yet implemented")
    }
}