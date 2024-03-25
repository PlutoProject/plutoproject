package ink.pmc.common.server.impl.message

import ink.pmc.common.server.message.Channel
import ink.pmc.common.server.message.Message
import ink.pmc.common.server.message.MessageManager
import ink.pmc.common.server.message.Reply
import ink.pmc.common.server.request.Response
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

abstract class AbstractMessageManager : MessageManager {

    override val inboundQueue: Queue<Message> = ConcurrentLinkedQueue()
    override val outboundQueue: Queue<Message> = ConcurrentLinkedQueue()
    override val channels: MutableMap<String, Channel> = ConcurrentHashMap()
    override val replies: MutableMap<UUID, Reply> = ConcurrentHashMap()
    override val responses: MutableMap<UUID, Response> = ConcurrentHashMap()

}