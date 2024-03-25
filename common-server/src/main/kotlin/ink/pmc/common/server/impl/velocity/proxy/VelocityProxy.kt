package ink.pmc.common.server.impl.velocity.proxy

import ink.pmc.common.server.PlatformType
import ink.pmc.common.server.Server
import ink.pmc.common.server.ServerStatus
import ink.pmc.common.server.gameserver.GameServer
import ink.pmc.common.server.network.Proxy
import ink.pmc.common.server.network.ServerDefinition
import ink.pmc.common.server.player.ServerPlayer
import ink.pmc.common.server.velocity.player
import ink.pmc.common.utils.player.switchServer
import java.util.*

@Suppress("UNUSED")
class VelocityProxy(
    override val id: Long,
    override val name: String
) : Proxy {

    override val backendServers: MutableSet<GameServer> = mutableSetOf()
    override val definedServers: MutableSet<ServerDefinition> = mutableSetOf()

    override fun transferServer(player: ServerPlayer, server: Server) {
        player.player.switchServer(server.name)
    }

    override val identity: UUID = UUID.randomUUID()
    override val platform: PlatformType = PlatformType.VELOCITY
    override var status: ServerStatus = ServerStatus.LOCAL
    override val players: MutableSet<ServerPlayer> = mutableSetOf()

}