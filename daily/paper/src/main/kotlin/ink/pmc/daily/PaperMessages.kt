package ink.pmc.daily

import ink.pmc.advkt.component.component
import ink.pmc.advkt.component.text
import ink.pmc.utils.chat.ECONOMY_SYMBOL
import ink.pmc.utils.visual.mochaPink
import ink.pmc.utils.visual.mochaSubtext0
import ink.pmc.utils.visual.mochaText

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