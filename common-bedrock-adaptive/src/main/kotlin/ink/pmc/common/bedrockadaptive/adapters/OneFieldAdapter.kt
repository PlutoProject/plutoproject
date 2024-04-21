package ink.pmc.common.bedrockadaptive.adapters

import com.comphenix.protocol.PacketType.Play
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import ink.pmc.common.bedrockadaptive.plugin

object OneFieldAdapter : PacketAdapter(
    plugin,
    ListenerPriority.HIGHEST,
    Play.Server.SYSTEM_CHAT,
    Play.Server.CHAT,
    Play.Server.SET_TITLE_TEXT,
    Play.Server.SET_SUBTITLE_TEXT,
    Play.Server.SET_ACTION_BAR_TEXT,
    Play.Server.KICK_DISCONNECT,
    Play.Server.RESOURCE_PACK_SEND,
    Play.Server.BOSS,
    Play.Server.COMMANDS,
    Play.Server.DISGUISED_CHAT,
    Play.Server.MAP,
) {

    override fun onPacketSending(event: PacketEvent?) {
        if (event == null) {
            return
        }

        val packet = event.packet
        val components = packet.chatComponents
        val size = components.size()

        if (size == 0) {
            return
        }

        for (i in 0..<size) {
            processPacket(packet, i)
        }
    }

}