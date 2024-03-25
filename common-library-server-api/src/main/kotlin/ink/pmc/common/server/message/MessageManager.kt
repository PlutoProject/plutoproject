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
    val replies: MutableMap<UUID, Reply>
    val responses: MutableMap<UUID, Response>

    fun getReplies(message: Message): Set<Reply> {
        return replies.keys.filter { it == message.uniqueId }.map { replies[it]!! }.toSet()
    }

    fun getReplies(message: UUID): Set<Reply> {
        return replies.keys.filter { it == message }.map { replies[it]!! }.toSet()
    }

    fun getResponse(request: Request): Set<Response> {
        return responses.keys.filter { it == request.uniqueId }.map { responses[it]!! }.toSet()
    }

    fun getResponse(request: UUID): Set<Response> {
        return responses.keys.filter { it == request }.map { responses[it]!! }.toSet()
    }

    fun sendOutbound(message: Message) {
        outboundQueue.add(message)
    }

    fun createChannel(name: String)

    fun registerChannel(name: String, uuid: String)

    fun clearReplies(uuid: UUID) {
        replies.entries.removeIf { it.key == uuid }
    }

    fun clearResponses(uuid: UUID) {
        responses.entries.removeIf { it.key == uuid }
    }

}