package ink.pmc.common.hypervisor

import ink.pmc.common.utils.roundTwoDecimals
import me.lucko.spark.api.statistic.StatisticWindow

val sparkTPS5Secs: Double
    get() = spark.tps()!!.poll(StatisticWindow.TicksPerSecond.SECONDS_5).roundTwoDecimals

val sparkTPS1Mins: Double
    get() = spark.tps()!!.poll(StatisticWindow.TicksPerSecond.MINUTES_1).roundTwoDecimals

val sparkTPS5Mins: Double
    get() = spark.tps()!!.poll(StatisticWindow.TicksPerSecond.MINUTES_5).roundTwoDecimals

val sparkMSPT10Secs: Double
    get() = spark.mspt()!!.poll(StatisticWindow.MillisPerTick.SECONDS_10).mean().roundTwoDecimals

val sparkMSPT1Min: Double
    get() = spark.mspt()!!.poll(StatisticWindow.MillisPerTick.MINUTES_1).mean().roundTwoDecimals