package ink.pmc.common.server.gameserver

import ink.pmc.common.server.Server

interface GameServer : Server {

    val chunkCount: Int
    val entityChunk: Int

}