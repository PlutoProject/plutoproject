package ink.pmc.common.utils.chat

import ink.pmc.common.utils.visual.mochaMaroon
import net.kyori.adventure.text.Component

@Suppress("UNUSED")
val NON_PLAYER
    get() = Component.text("该命令仅限玩家使用").color(mochaMaroon)

val EMPTY_LINE
    get() = Component.text(" ")