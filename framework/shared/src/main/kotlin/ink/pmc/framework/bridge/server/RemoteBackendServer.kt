package ink.pmc.framework.bridge.server

import ink.pmc.framework.bridge.player.BridgePlayer
import ink.pmc.framework.bridge.world.BridgeWorld
import ink.pmc.framework.utils.data.mutableConcurrentListOf

class RemoteBackendServer(override val id: String, override val group: BridgeGroup?) : InternalServer() {
    override val type: ServerType = ServerType.BACKEND
    override val state: ServerState = ServerState.REMOTE
    override val worlds: MutableList<BridgeWorld> = mutableConcurrentListOf()
    override var isOnline: Boolean = true
    override val players: MutableList<BridgePlayer> = mutableConcurrentListOf()
}