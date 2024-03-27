package ink.pmc.common.server.impl.velocity.message

import ink.pmc.common.server.encodeMsg
import ink.pmc.common.server.encodeReq
import ink.pmc.common.server.impl.message.AbstractMessageManager
import ink.pmc.common.server.impl.velocity.VelocityServerService
import ink.pmc.common.server.message.Channel
import ink.pmc.common.server.message.Message
import ink.pmc.common.server.request.Request
import ink.pmc.common.utils.concurrent.submitAsync
import io.netty.channel.socket.SocketChannel

class VelocityMessageManager(private val serverService: VelocityServerService) : AbstractMessageManager() {

    private var closed = false

    private fun runInboundProcessJob() {
        submitAsync {
            while (!closed) {
                val iterator = inboundQueue.iterator()

                while (iterator.hasNext()) {
                    val message = inboundQueue.peek()

                    if (p2sShouldForward(message)) {
                        p2sForward(message)
                        continue
                    }

                    if (p2sSelfReceive(message)) {
                    }
                }
            }
        }
    }

    private fun p2sShouldForward(message: Message): Boolean {
        val receivers = message.receivers
        return p2sSelfReceive(message) && receivers.size == 1
    }

    private fun p2sSelfReceive(message: Message): Boolean {
        val receivers = message.receivers
        return receivers.contains(serverService.server.identity)
    }

    private fun p2sForward(message: Message) {
        val channels = serverService.verifiedClientIdsToChannelMap.entries.filter { message.receivers.contains(it.key) }
            .map { it.value }.toSet()

        if (message is Request) {
            p2sRequestForward(message, channels)
            return
        }

        p2sMessageForward(message, channels)
    }

    private fun p2sMessageForward(message: Message, channels: Set<SocketChannel>) {
        channels.forEach {
            it.writeAndFlush(encodeMsg(message))
        }
    }

    private fun p2sRequestForward(request: Request, channels: Set<SocketChannel>) {
        channels.forEach {
            it.writeAndFlush(encodeReq(request))
        }
    }

    init {
        runInboundProcessJob()
    }

    override fun createChannel(name: String): Channel {
        TODO("Not yet implemented")
    }

    override fun registerChannel(name: String, uuid: String): Channel {
        TODO("Not yet implemented")
    }

    override fun close() {
        closed = true
    }

}