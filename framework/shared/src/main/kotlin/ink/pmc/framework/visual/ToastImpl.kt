package ink.pmc.framework.visual

import ink.pmc.utils.item.KeyedMaterial
import ink.pmc.visual.api.toast.Toast
import ink.pmc.visual.api.toast.ToastFrame
import ink.pmc.visual.api.toast.ToastType
import net.kyori.adventure.text.Component

data class ToastImpl(
    override val icon: KeyedMaterial,
    override val message: Component,
    override val type: ToastType,
    override val frame: ToastFrame
) : Toast