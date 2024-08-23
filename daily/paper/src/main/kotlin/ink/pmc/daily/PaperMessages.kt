package ink.pmc.daily

import ink.pmc.advkt.component.component
import ink.pmc.advkt.component.text
import ink.pmc.utils.chat.ECONOMY_SYMBOL
import ink.pmc.utils.visual.mochaLavender
import ink.pmc.utils.visual.mochaPink
import ink.pmc.utils.visual.mochaSubtext0
import ink.pmc.utils.visual.mochaText
import net.kyori.adventure.text.Component

val CHECK_IN = component {
    text("到访成功，本月已连续到访 ") with mochaPink
    text("<acc> ") with mochaText
    text("天") with mochaPink
}

val CHECKED_IN = component {
    text("今日已到访") with mochaSubtext0
}

val COIN_CLAIM = component {
    text("今日到访获得 ") with mochaSubtext0
    text("<amount>$ECONOMY_SYMBOL") with mochaText
}

val UI_TITLE = component {
    text("礼记日历 | <year>/<month>/<day>")
}

val NAVIGATE = component {
    text("<year> 年 <month> 月") with mochaText
}

private val NAVIGATE_LORE_PREV = component {
    text("左键 ") with mochaLavender
    text("上一页 ") with mochaText
    text("(<prevYear> 年 <prevMonth> 月)") with mochaSubtext0
}

private val NAVIGATE_LORE_NEXT = component {
    text("右键 ") with mochaLavender
    text("下一页 ") with mochaText
    text("(<nextYear> 年 <nextMonth> 月)") with mochaSubtext0
}

private val NAVIGATE_LORE_RESET = component {
    text("Shift + 左键 ") with mochaLavender
    text("回到现在") with mochaText
}

val NAVIGATE_LORE = listOf(
    Component.empty(),
    NAVIGATE_LORE_PREV,
    NAVIGATE_LORE_NEXT,
    NAVIGATE_LORE_RESET
)

val NAVIGATE_PREV_REACHED = component {
    text("仅限查看最近 ") with mochaSubtext0
    text("12 ") with mochaText
    text("个月的记录") with mochaSubtext0
}

val NAVIGATE_LORE_PREV_REACHED = listOf(
    Component.empty(),
    NAVIGATE_PREV_REACHED,
    NAVIGATE_LORE_NEXT,
    NAVIGATE_LORE_RESET
)