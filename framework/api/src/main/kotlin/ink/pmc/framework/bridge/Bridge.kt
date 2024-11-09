package ink.pmc.framework.bridge

import ink.pmc.framework.bridge.player.BridgePlayer
import ink.pmc.framework.bridge.player.PlayerLookup
import ink.pmc.framework.bridge.server.*
import ink.pmc.framework.utils.inject.inlinedGet
import java.util.*

interface Bridge : PlayerLookup, ServerLookup {
    companion object : Bridge by inlinedGet()

    val local: BridgeServer
    val master: BridgeServer
        get() = servers.first { it.id == "_master" }
    val groups: Collection<BridgeGroup>
    override val players: Collection<BridgePlayer>
        get() = servers.flatMap { it.players }.sortedBy {
            when {
                it.isLocal -> if (local.type == ServerType.PROXY) 1 else 0
                it.isRemoteBackend -> if (local.type == ServerType.PROXY) 0 else 1
                it.isRemoteProxy -> 2
                else -> error("Unexpected")
            }
        }.distinctBy { it.uniqueId }

    fun getGroup(id: String): BridgeGroup?

    fun isGroupRegistered(id: String): Boolean

    private fun filterPlayer(state: ServerState?, type: ServerType?): List<BridgePlayer> {
        return servers.flatMap { it.players }.filter {
            it.serverState == (state ?: it.serverState) && it.serverType == (type ?: it.serverType)
        }
    }

    override fun getPlayer(name: String, state: ServerState?, type: ServerType?): BridgePlayer? {
        if (state != null || type != null) {
            return filterPlayer(state, type).firstOrNull { it.name == name }
        }
        return super.getPlayer(name, null, null)
    }

    override fun getPlayer(uniqueId: UUID, state: ServerState?, type: ServerType?): BridgePlayer? {
        if (state != null || type != null) {
            return filterPlayer(state, type).firstOrNull { it.uniqueId == uniqueId }
        }
        return super.getPlayer(uniqueId, null, null)
    }

    override fun getRemotePlayer(name: String): BridgePlayer? {
        return servers.flatMap { it.players }.firstOrNull { it.name == name && !it.isLocal }
    }

    override fun getRemotePlayer(uniqueId: UUID): BridgePlayer? {
        return servers.flatMap { it.players }.firstOrNull { it.uniqueId == uniqueId && !it.isLocal }
    }
}