package ink.pmc.common.bedrockadaptive.velocity

import com.velocitypowered.proxy.protocol.packet.BossBarPacket
import dev.simplix.protocolize.api.Direction
import dev.simplix.protocolize.api.listener.AbstractPacketListener
import dev.simplix.protocolize.api.listener.PacketReceiveEvent
import dev.simplix.protocolize.api.listener.PacketSendEvent
import ink.pmc.common.bedrockadaptive.utils.replaceFallbackColor
import ink.pmc.common.member.api.session.SessionService

object BossBarPacketListener : AbstractPacketListener<BossBarPacket>(BossBarPacket::class.java, Direction.UPSTREAM, 0) {

    override fun packetReceive(event: PacketReceiveEvent<BossBarPacket>) {
    }

    override fun packetSend(event: PacketSendEvent<BossBarPacket>) {
        if (!SessionService.isBedrockSession(event.player().uniqueId())) {
            return
        }

        replaceFallbackColor(event.packet(), "name")
    }

}