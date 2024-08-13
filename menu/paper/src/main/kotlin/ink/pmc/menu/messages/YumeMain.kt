package ink.pmc.menu.messages

import ink.pmc.advkt.component.component
import ink.pmc.advkt.component.italic
import ink.pmc.advkt.component.text
import ink.pmc.utils.visual.mochaLavender
import ink.pmc.utils.visual.mochaText
import net.kyori.adventure.text.Component

val YUME_MAIN_TITLE = component {
    text("手账")
}

val YUME_MAIN_TAB_LORE = listOf(
    Component.empty(),
    component {
        text("左键 ") with mochaLavender without italic()
        text("切换至该选项卡") with mochaText without italic()
    }
)

val YUME_MAIN_ITEM_COMMON = component {
    text("常用功能") with mochaText without italic()
}