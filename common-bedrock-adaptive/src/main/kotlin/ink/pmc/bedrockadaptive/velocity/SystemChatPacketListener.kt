package ink.pmc.bedrockadaptive.velocity

import com.velocitypowered.proxy.protocol.packet.chat.ChatType
import com.velocitypowered.proxy.protocol.packet.chat.SystemChatPacket
import dev.simplix.protocolize.api.Direction
import dev.simplix.protocolize.api.listener.AbstractPacketListener
import dev.simplix.protocolize.api.listener.PacketReceiveEvent
import dev.simplix.protocolize.api.listener.PacketSendEvent
import ink.pmc.bedrockadaptive.utils.replaceFallbackColor
import ink.pmc.utils.bedrock.isFloodgatePlayer

@Suppress("UNUSED")
object SystemChatPacketListener : AbstractPacketListener<SystemChatPacket>(
    SystemChatPacket::class.java,
    Direction.UPSTREAM,
    0
) {

    override fun packetReceive(event: PacketReceiveEvent<SystemChatPacket>) {
    }

    override fun packetSend(event: PacketSendEvent<SystemChatPacket>) {
        val packet = event.packet()
        val chatTypeField = packet.javaClass.getDeclaredField("type")
        val type = if (packet.type == ChatType.CHAT) ChatType.SYSTEM else packet.type

        chatTypeField.isAccessible = true
        chatTypeField.set(packet, type)

        if (!isFloodgatePlayer(event.player().uniqueId())) {
            return
        }

        replaceFallbackColor(packet)
    }

}