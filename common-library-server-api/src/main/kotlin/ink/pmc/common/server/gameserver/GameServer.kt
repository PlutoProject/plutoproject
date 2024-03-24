package ink.pmc.common.server.gameserver

import ink.pmc.common.server.Server
import ink.pmc.common.server.ServerService
import ink.pmc.common.server.entity.ServerEntity
import ink.pmc.common.server.player.ServerPlayer

@Suppress("UNUSED")
interface GameServer : Server {

    val chunkCount: Int
    val entities: Set<ServerEntity>
    val entityChunk: Int
        get() = entities.size

    fun sendPlayer(player: ServerPlayer) {
        ServerService.instance.network.proxy.transferServer(player, this)
    }

}