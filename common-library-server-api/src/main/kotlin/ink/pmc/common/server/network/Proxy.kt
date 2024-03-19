package ink.pmc.common.server.network

import ink.pmc.common.server.Server
import ink.pmc.common.server.gameserver.GameServer

interface Proxy : Server {

    val backendServers: Set<GameServer>
    val definedServers: Set<ServerDefinition>

}