package ink.pmc.bedrockadaptive.utils

import com.velocitypowered.proxy.protocol.MinecraftPacket
import com.velocitypowered.proxy.protocol.packet.chat.ComponentHolder
import ink.pmc.bedrockadaptive.protocolVersion
import ink.pmc.utils.bedrock.useBedrockColors

fun replaceFallbackColor(packet: MinecraftPacket, holderFieldName: String = "component") {
    val componentHolderField = packet.javaClass.getDeclaredField(holderFieldName)
    componentHolderField.isAccessible = true
    val componentHolderObject = componentHolderField.get(packet) ?: return
    val componentHolder = componentHolderObject as ComponentHolder
    val component = componentHolder.component.useBedrockColors()
    val newComponentHolder = ComponentHolder(protocolVersion, component)

    componentHolderField.set(packet, newComponentHolder)
}