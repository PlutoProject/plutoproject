package ink.pmc.framework.bridge.server

import ink.pmc.framework.bridge.Bridge
import ink.pmc.framework.bridge.player.BridgePlayer

data class BridgeGroupImpl(override val id: String) : BridgeGroup {
    override val players: Collection<BridgePlayer>
        get() = Bridge.players.filter { it.group == this }
    override val servers: Collection<BridgeServer>
        get() = Bridge.servers.filter { it.group == this }
}