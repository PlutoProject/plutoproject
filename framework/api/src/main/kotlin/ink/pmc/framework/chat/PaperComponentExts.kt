package ink.pmc.framework.chat

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.minecraft.core.RegistryAccess

typealias NmsComponent = net.minecraft.network.chat.Component
typealias NmsComponentSerializer = net.minecraft.network.chat.Component.Serializer

val Component.internal: NmsComponent
    get() {
        val json = GsonComponentSerializer.gson().serialize(this)
        return NmsComponentSerializer.fromJson(json, RegistryAccess.EMPTY) as NmsComponent
    }