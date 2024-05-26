package ink.pmc.bedrockadaptive.utils

import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.wrappers.WrappedChatComponent
import ink.pmc.utils.bedrock.useFallbackColors
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer

@Suppress("UNUSED")
fun handlePacket(packet: PacketContainer) {
    val size = packet.chatComponents.size()

    for (i in 0..<size) {
        val wrappedComponentJson = packet.chatComponents.read(i).json
        val adventureComponent = GsonComponentSerializer.gson().deserialize(wrappedComponentJson)
        val newComponent = adventureComponent.useFallbackColors()
        val newComponentJson = GsonComponentSerializer.gson().serialize(newComponent)
        val newWrappedComponent = WrappedChatComponent.fromJson(newComponentJson)
        packet.chatComponents.write(i, newWrappedComponent)
    }
}