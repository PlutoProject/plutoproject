package ink.pmc.daily

import ink.pmc.advkt.component.component
import ink.pmc.advkt.component.italic
import ink.pmc.advkt.component.text
import ink.pmc.utils.chat.ECONOMY_SYMBOL
import ink.pmc.utils.visual.*
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
    text("<year> 年 <month> 月") with mochaText without italic()
}

private val NAVIGATE_LORE_PREV = component {
    text("左键 ") with mochaLavender without italic()
    text("上一页 ") with mochaText without italic()
    text("(<prevYear> 年 <prevMonth> 月)") with mochaSubtext0 without italic()
}

private val NAVIGATE_LORE_NEXT = component {
    text("右键 ") with mochaLavender without italic()
    text("下一页 ") with mochaText without italic()
    text("(<nextYear> 年 <nextMonth> 月)") with mochaSubtext0 without italic()
}

private val NAVIGATE_LORE_RESET = component {
    text("中键 ") with mochaLavender without italic()
    text("回到现在") with mochaText without italic()
}

val NAVIGATE_LORE = listOf(
    Component.empty(),
    NAVIGATE_LORE_PREV,
    NAVIGATE_LORE_NEXT,
    NAVIGATE_LORE_RESET
)

val NAVIGATE_PREV_REACHED = component {
    text("仅限查看最近 ") with mochaSubtext0 without italic()
    text("12 ") with mochaText without italic()
    text("个月的记录") with mochaSubtext0 without italic()
}

val NAVIGATE_LORE_PREV_REACHED = listOf(
    Component.empty(),
    NAVIGATE_PREV_REACHED,
    NAVIGATE_LORE_NEXT,
    NAVIGATE_LORE_RESET
)

val DAY = component {
    text("<year>/<month>/<day>") with mochaText without italic()
}

private val DAY_UNCHECKED_IN = component {
    text("本日未到访") with mochaSubtext0 without italic()
}

private val DAY_CHECKED_IN = component {
    text("√ 已到访") with mochaGreen without italic()
}

private val DAY_CHECK_IN_OPREATION = component {
    text("左键 ") with mochaLavender without italic()
    text("到访一次") with mochaText without italic()
}

val DAY_LORE = listOf(
    DAY_UNCHECKED_IN,
    Component.empty(),
    DAY_CHECK_IN_OPREATION
)

val DAY_LORE_CHECKED_IN = listOf(
    DAY_CHECKED_IN
)

val DAY_LORE_PAST = listOf(
    DAY_UNCHECKED_IN
)

val DAY_LORE_FUTURE = listOf(
    component {
        text("不远的将来...") with mochaSubtext0 without italic()
    }
)