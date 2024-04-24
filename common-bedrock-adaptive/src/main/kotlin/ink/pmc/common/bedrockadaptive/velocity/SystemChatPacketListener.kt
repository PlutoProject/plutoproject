package ink.pmc.common.bedrockadaptive.velocity

import com.velocitypowered.proxy.protocol.packet.chat.ChatType
import com.velocitypowered.proxy.protocol.packet.chat.SystemChatPacket
import dev.simplix.protocolize.api.Direction
import dev.simplix.protocolize.api.listener.AbstractPacketListener
import dev.simplix.protocolize.api.listener.PacketReceiveEvent
import dev.simplix.protocolize.api.listener.PacketSendEvent
import ink.pmc.common.bedrockadaptive.utils.replaceFallbackColor
import ink.pmc.common.member.api.session.SessionService

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
        val type = ChatType.SYSTEM

        /*
        * Protocolize 接管数据包后会由于未知原因将 ChatType 换为 CHAT。
        * 这里强行换成 SYSTEM 以避免编码错误。
        * */
        chatTypeField.isAccessible = true
        chatTypeField.set(packet, type)

        if (!SessionService.isBedrockSession(event.player().uniqueId())) {
            return
        }

        replaceFallbackColor(packet)
    }

}