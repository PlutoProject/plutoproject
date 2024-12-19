package ink.pmc.framework.visual

import ink.pmc.framework.item.KeyedMaterial
import ink.pmc.framework.visual.toast.ToastFrame
import ink.pmc.framework.visual.toast.ToastType
import net.kyori.adventure.text.Component

data class ToastImpl(
    override val icon: KeyedMaterial,
    override val message: Component,
    override val type: ToastType,
    override val frame: ToastFrame
) : ink.pmc.framework.visual.toast.Toast