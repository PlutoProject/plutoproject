package ink.pmc.daily

import ink.pmc.daily.api.PostCheckInCallback
import ink.pmc.framework.utils.chat.replace
import ink.pmc.framework.utils.trimmed
import org.koin.java.KoinJavaComponent.getKoin
import java.time.LocalDate

val coinReward: PostCheckInCallback = { user ->
    val config = getKoin().get<DailyConfig>()
    val date = LocalDate.now()
    val base = if (date.dayOfWeek.value in 1..5) config.weekdayReward else config.weekendReward
    val accumulate = if (user.accumulatedDays % config.accRequirements == 0) config.accumulateReward else 0.0
    val amount = base + accumulate
    economy.depositPlayer(user.player, amount)
    user.player.player?.sendMessage(COIN_CLAIM.replace("<amount>", amount.trimmed()))
}