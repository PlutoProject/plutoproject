package ink.pmc.bedrockadaptive

import com.velocitypowered.proxy.protocol.packet.HeaderAndFooterPacket
import dev.simplix.protocolize.api.Direction
import dev.simplix.protocolize.api.listener.AbstractPacketListener
import dev.simplix.protocolize.api.listener.PacketReceiveEvent
import dev.simplix.protocolize.api.listener.PacketSendEvent
import ink.pmc.bedrockadaptive.utils.replaceFallbackColor
import ink.pmc.utils.bedrock.isFloodgatePlayer

@Suppress("UNUSED")
object HeaderAndFooterPacketListener : AbstractPacketListener<HeaderAndFooterPacket>(
    HeaderAndFooterPacket::class.java,
    Direction.UPSTREAM,
    0
) {

    override fun packetReceive(event: PacketReceiveEvent<HeaderAndFooterPacket>) {
    }

    override fun packetSend(event: PacketSendEvent<HeaderAndFooterPacket>) {
        if (!isFloodgatePlayer(event.player().uniqueId())) {
            return
        }

        replaceFallbackColor(event.packet(), "header")
        replaceFallbackColor(event.packet(), "footer")
    }

}