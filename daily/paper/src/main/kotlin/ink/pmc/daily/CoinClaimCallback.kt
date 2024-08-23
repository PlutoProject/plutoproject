package ink.pmc.daily

import ink.pmc.advkt.component.newline
import ink.pmc.advkt.component.text
import ink.pmc.advkt.send
import ink.pmc.daily.api.PostCheckInCallback
import ink.pmc.utils.chat.ECONOMY_SYMBOL
import ink.pmc.utils.visual.mochaPink
import ink.pmc.utils.visual.mochaSubtext0
import ink.pmc.utils.visual.mochaText
import java.time.LocalDate

val coinClaim: PostCheckInCallback = { user ->
    val date = LocalDate.now()
    val amount = if (date.dayOfWeek.value in 1..5) 2 else 5
    val accumulate = if (user.accumulatedDays % 7 == 0) 10 else 0
    economy.depositPlayer(user.player, amount + accumulate.toDouble())

    user.player.player?.send {
        text("今日到访获取 ") with mochaPink
        text("${amount + accumulate}$ECONOMY_SYMBOL") with mochaText
        if (accumulate != 0) {
            newline()
            text("已连续到访七天，额外获得 ") with mochaSubtext0
            text("$accumulate$ECONOMY_SYMBOL")
        }
    }
}