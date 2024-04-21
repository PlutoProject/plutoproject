package ink.pmc.common.bedrockadaptive.adapters

import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.wrappers.WrappedChatComponent
import ink.pmc.common.utils.bedrock.useFallbackColors
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer

fun processPacket(packet: PacketContainer, vararg fields: Int) {
    fields.forEach {
        val wrapped = packet.chatComponents.read(it) ?: return
        val component = GsonComponentSerializer.gson().deserialize(wrapped.json).useFallbackColors()
        val serializedBack = GsonComponentSerializer.gson().serialize(component)
        packet.chatComponents.write(it, WrappedChatComponent.fromJson(serializedBack))
    }
}