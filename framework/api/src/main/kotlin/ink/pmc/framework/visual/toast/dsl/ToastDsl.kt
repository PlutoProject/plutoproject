package ink.pmc.framework.visual.toast.dsl

import ink.pmc.advkt.component.RootComponentKt
import ink.pmc.framework.item.KeyedMaterial
import ink.pmc.framework.visual.toast.Toast
import ink.pmc.framework.visual.toast.ToastFactory
import ink.pmc.framework.visual.toast.ToastFrame
import ink.pmc.framework.visual.toast.ToastType
import net.kyori.adventure.text.Component

@Suppress("UNUSED")
class ToastDsl {

    private lateinit var iconVar: KeyedMaterial
    private lateinit var messageVar: Component
    private lateinit var typeVar: ToastType
    private lateinit var frameVar: ToastFrame

    fun icon(icon: KeyedMaterial) {
        this.iconVar = icon
    }

    fun message(component: Component) {
        this.messageVar = component
    }

    fun message(content: RootComponentKt.() -> Unit) {
        this.messageVar = RootComponentKt().apply(content).build()
    }

    fun type(type: ToastType) {
        this.typeVar = type
    }

    fun frame(frame: ToastFrame) {
        this.frameVar = frame
    }

    fun create(): Toast {
        return ToastFactory.of(
            if (!::iconVar.isInitialized) KeyedMaterial("minecraft:air") else iconVar,
            if (!::messageVar.isInitialized) Component.empty() else messageVar,
            if (!::typeVar.isInitialized) ToastType.TASK else typeVar,
            if (!::frameVar.isInitialized) ToastFrame.ADVENTURE else frameVar
        )
    }

}

fun toast(content: ToastDsl.() -> Unit): Toast {
    return ToastDsl().apply(content).create()
}