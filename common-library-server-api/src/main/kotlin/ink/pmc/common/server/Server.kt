package ink.pmc.common.server

import ink.pmc.common.server.message.Message
import ink.pmc.common.server.player.ServerPlayer
import java.util.*

@Suppress("UNUSED")
interface Server {

    val id: Long
    val identity: UUID
    val name: String
    val platform: PlatformType
    var status: ServerStatus
    val players: Set<ServerPlayer>
    val playerCount: Int
        get() = players.size

    fun sendMessage(content: String): Message {
        return ServerService.instance.channel.sendMessage(content, this)
    }

    fun replyMessage(message: UUID, content: String): Message {
        return ServerService.instance.channel.replyMessage(message, content)
    }

    fun replyMessage(message: Message, content: String): Message {
        return ServerService.instance.channel.replyMessage(message, content)
    }

    fun getPlayer(uuid: UUID): ServerPlayer? {
        val filtered = players.filter { it.uniqueID == uuid }

        if (filtered.isEmpty()) {
            return null
        }

        return filtered.first()
    }

    fun isOnline(uuid: UUID): Boolean {
        return players.any { it.uniqueID == uuid }
    }

}