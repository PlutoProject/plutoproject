package ink.pmc.bedrockadaptive

import com.velocitypowered.proxy.protocol.packet.title.TitleTextPacket
import dev.simplix.protocolize.api.Direction
import dev.simplix.protocolize.api.listener.AbstractPacketListener
import dev.simplix.protocolize.api.listener.PacketReceiveEvent
import dev.simplix.protocolize.api.listener.PacketSendEvent
import ink.pmc.bedrockadaptive.utils.replaceFallbackColor
import ink.pmc.utils.bedrock.isFloodgatePlayer

@Suppress("UNUSED")
object TitleTextPacketListener : AbstractPacketListener<TitleTextPacket>(
    TitleTextPacket::class.java,
    Direction.UPSTREAM,
    0
) {

    override fun packetReceive(event: PacketReceiveEvent<TitleTextPacket>) {
    }

    override fun packetSend(event: PacketSendEvent<TitleTextPacket>) {
        if (!isFloodgatePlayer(event.player().uniqueId())) {
            return
        }

        replaceFallbackColor(event.packet())
    }

}