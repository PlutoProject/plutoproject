package ink.pmc.utils.multiplaform

import ink.pmc.advkt.component.RootComponentKt
import net.kyori.adventure.text.Component

interface SenderWrapper<T> : Wrapper<T> {

    fun sendMessage(content: Component)

    fun sendMessage(content: RootComponentKt.() -> Unit)

}