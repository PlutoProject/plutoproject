package ink.pmc.common.server

import ink.pmc.common.server.message.Message
import ink.pmc.common.server.message.MessageManager
import ink.pmc.common.server.network.Network
import ink.pmc.common.server.player.ServerPlayer
import java.util.*

@Suppress("UNUSED")
interface Server {

    val uniqueId: UUID
    val name: String
    val platform: PlatformType
    val status: ServerStatus
    val network: Network?
    val playerCount: Int
    val messageManager: MessageManager

    val isInNetwork: Boolean
        get() = network != null

    fun sendMessage(content: String): Message

    fun replyMessage(content: String, messageToReply: UUID): Message

    fun replyMessage(content: String, messageToReply: Message): Message

    fun getPlayer(uuid: UUID): ServerPlayer?

    fun isOnline(uuid: UUID): Boolean

    fun sendPlayer(player: ServerPlayer)

    fun sendHeartbeat()

}