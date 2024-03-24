package ink.pmc.common.server.impl.paper

import ink.pmc.common.server.PlatformType
import ink.pmc.common.server.Server
import ink.pmc.common.server.ServerStatus
import ink.pmc.common.server.network.Network
import ink.pmc.common.server.player.ServerPlayer
import java.util.*

@Suppress("UNUSED")
class PaperServer(
    override val id: Long,
    override val identity: UUID,
    override val name: String,
    override val platform: PlatformType,
    isRemote: Boolean,
    override val network: Network,
    override val players: Set<ServerPlayer>
) : Server {

    override var status: ServerStatus =
        if (isRemote) {
            ServerStatus.REMOTE
        } else {
            ServerStatus.LOCAL
        }

}