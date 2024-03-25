package ink.pmc.common.server

import ink.pmc.common.server.message.Channel
import ink.pmc.common.server.message.MessageManager
import ink.pmc.common.server.network.Network

@Suppress("UNUSED")
interface ServerService {

    companion object {
        lateinit var instance: ServerService
    }

    val network: Network
    val server: Server
    val messageManager: MessageManager
    var channel: Channel

}