package ink.pmc.common.server.impl.paper

import ink.pmc.common.server.Server
import ink.pmc.common.server.ServerService
import ink.pmc.common.server.message.Channel
import ink.pmc.common.server.message.MessageManager
import ink.pmc.common.server.network.Network

@Suppress("UNUSED")
class PaperServerService(val token: String, val address: String) : ServerService {

    override val network: Network
        get() = TODO("Not yet implemented")
    override val server: Server
        get() = TODO("Not yet implemented")
    override val messageManager: MessageManager
        get() = TODO("Not yet implemented")
    override val channel: Channel
        get() = TODO("Not yet implemented")

}