package ink.pmc.bedrockadaptive

import dev.simplix.protocolize.api.Direction
import dev.simplix.protocolize.api.chat.ChatElement
import dev.simplix.protocolize.api.item.BaseItemStack
import dev.simplix.protocolize.api.listener.AbstractPacketListener
import dev.simplix.protocolize.api.listener.PacketReceiveEvent
import dev.simplix.protocolize.api.listener.PacketSendEvent
import dev.simplix.protocolize.data.packets.WindowItems
import ink.pmc.utils.bedrock.isFloodgatePlayer
import ink.pmc.utils.bedrock.useFallbackColors
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer

@Suppress("UNUSED")
object InventoryItemPacketListener : AbstractPacketListener<WindowItems>(
    WindowItems::class.java,
    Direction.UPSTREAM,
    0
) {

    private val serializer = GsonComponentSerializer.gson()

    override fun packetReceive(event: PacketReceiveEvent<WindowItems>) {
    }

    override fun packetSend(event: PacketSendEvent<WindowItems>) {
        if (!isFloodgatePlayer(event.player().uniqueId())) {
            return
        }

        val packet = event.packet()

        packet.items().forEach {
            replace(it)
        }

        replace(packet.cursorItem())
    }

    private fun replace(item: BaseItemStack) {
        val displayName = item.displayName()

        if (displayName != null) {
            val displayNameJson = displayName.asJson()
            val displayNameComponent = serializer.deserialize(displayNameJson).useFallbackColors()
            val displayNameComponentJson = serializer.serialize(displayNameComponent)
            item.displayName(ChatElement.ofJson<Any>(displayNameComponentJson))
        }

        val lore = mutableListOf<ChatElement<*>>()

        item.lore<Any>().forEach {
            val loreJson = it.asJson()
            val loreComponent = serializer.deserialize(loreJson).useFallbackColors()
            val loreComponentJson = serializer.serialize(loreComponent)
            lore.add(ChatElement.ofJson<Any>(loreComponentJson))
        }

        item.lore(lore)
    }
}