package ink.pmc.common.server.message

import ink.pmc.common.server.request.Request
import ink.pmc.common.server.request.Response
import java.io.Closeable
import java.util.*

@Suppress("UNUSED")
interface MessageManager : Closeable {

    val inboundQueue: Queue<Message>
    val outboundQueue: Queue<Message>
    val channels: MutableMap<String, Channel>
    val replies: MutableMap<UUID, MutableSet<Reply>>
    val responses: MutableMap<UUID, MutableSet<Response>>

    fun getReplies(message: Message): Set<Reply> {
        val set = replies[message.uniqueId] ?: return mutableSetOf()
        return set.toSet()
    }

    fun getReplies(message: UUID): Set<Reply> {
        val set = replies[message] ?: return mutableSetOf()
        return set.toSet()
    }

    fun getResponse(request: Request): Set<Response> {
        val set = responses[request.uniqueId] ?: return mutableSetOf()
        return set.toSet()
    }

    fun getResponse(request: UUID): Set<Response> {
        val set = responses[request] ?: return mutableSetOf()
        return set.toSet()
    }

    fun sendOutbound(message: Message) {
        outboundQueue.add(message)
    }

    fun createChannel(name: String): Channel

    fun registerChannel(name: String, uuid: String): Channel

    fun clearReplies(uuid: UUID) {
        replies.entries.removeIf { it.key == uuid }
    }

    fun clearResponses(uuid: UUID) {
        responses.entries.removeIf { it.key == uuid }
    }

}