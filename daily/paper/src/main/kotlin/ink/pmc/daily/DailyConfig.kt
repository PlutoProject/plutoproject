package ink.pmc.daily

import com.electronwill.nightconfig.core.Config

@Suppress("UNUSED")
class DailyConfig(config: Config) : Config by config {

    val postCheckInCommands: List<String> get() = get("post-check-in-commands")
    val weekdayReward: Double get() = get("coin-reward.weekday")
    val weekendReward: Double get() = get("coin-reward.weekend")
    val accumulateReward: Double get() = get("coin-reward.accumulate")
    val accRequirements: Int get() = get("coin-reward.accumulate-requirements")

}