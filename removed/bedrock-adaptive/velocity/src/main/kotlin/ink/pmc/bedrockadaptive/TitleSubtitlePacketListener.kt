package ink.pmc.bedrockadaptive

import com.velocitypowered.proxy.protocol.packet.title.TitleSubtitlePacket
import dev.simplix.protocolize.api.Direction
import dev.simplix.protocolize.api.listener.AbstractPacketListener
import dev.simplix.protocolize.api.listener.PacketReceiveEvent
import dev.simplix.protocolize.api.listener.PacketSendEvent
import ink.pmc.bedrockadaptive.utils.replaceFallbackColor
import ink.pmc.framework.utils.bedrock.isFloodgatePlayer

@Suppress("UNUSED")
object TitleSubtitlePacketListener : AbstractPacketListener<TitleSubtitlePacket>(
    TitleSubtitlePacket::class.java,
    Direction.UPSTREAM,
    0
) {

    override fun packetReceive(event: PacketReceiveEvent<TitleSubtitlePacket>) {
    }

    override fun packetSend(event: PacketSendEvent<TitleSubtitlePacket>) {
        if (!isFloodgatePlayer(event.player().uniqueId())) {
            return
        }

        replaceFallbackColor(event.packet())
    }

}