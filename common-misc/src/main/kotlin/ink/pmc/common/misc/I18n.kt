package ink.pmc.common.misc

import com.catppuccin.Palette
import ink.pmc.common.utils.toTextColor
import net.kyori.adventure.text.Component

val SUICIDE = Component.text("你终结了你自己...")
    .color(Palette.MOCHA.flamingo.toTextColor())

val STAND_UP = Component.text("使用 ").color(Palette.MOCHA.text.toTextColor())
    .append(Component.keybind("key.sneak").color(Palette.MOCHA.flamingo.toTextColor()))
    .append(Component.text(" 来站起").color(Palette.MOCHA.text.toTextColor()))

val ILLEGAL_LOC =
    Component.text("无法在此处坐下，请检查是否有实体方块和足够的空间").color(Palette.MOCHA.red.toTextColor())