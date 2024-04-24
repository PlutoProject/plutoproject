package ink.pmc.common.bedrockadaptive.velocity

import com.velocitypowered.proxy.protocol.packet.chat.session.SessionPlayerChatPacket
import dev.simplix.protocolize.api.Direction
import dev.simplix.protocolize.api.listener.AbstractPacketListener
import dev.simplix.protocolize.api.listener.PacketReceiveEvent
import dev.simplix.protocolize.api.listener.PacketSendEvent

object SessionPlayerChatPacketListener : AbstractPacketListener<SessionPlayerChatPacket>(
    SessionPlayerChatPacket::class.java,
    Direction.UPSTREAM,
    0
) {

    override fun packetReceive(event: PacketReceiveEvent<SessionPlayerChatPacket>) {
    }

    override fun packetSend(event: PacketSendEvent<SessionPlayerChatPacket>) {
        println(event.packet().message)
    }

}