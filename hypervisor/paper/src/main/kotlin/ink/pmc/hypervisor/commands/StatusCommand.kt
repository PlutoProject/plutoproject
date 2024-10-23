package ink.pmc.hypervisor.commands

import ink.pmc.advkt.component.component
import ink.pmc.advkt.component.newline
import ink.pmc.advkt.component.raw
import ink.pmc.advkt.component.text
import ink.pmc.advkt.send
import ink.pmc.hypervisor.LoadLevel.*
import ink.pmc.hypervisor.MeasuringTime
import ink.pmc.hypervisor.StatisticProvider
import ink.pmc.utils.concurrent.sync
import ink.pmc.utils.platform.paper
import ink.pmc.utils.roundToTwoDecimals
import ink.pmc.utils.visual.*
import net.kyori.adventure.text.Component
import org.bukkit.command.CommandSender
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Permission

@Suppress("UNUSED")
object StatusCommand {
    @Command("status|tps|mspt")
    @Permission("hypervisor.status")
    suspend fun status(sender: CommandSender) {
        sync {
            sender.send {
                text("» ") with mochaSubtext0
                text("\uD83D\uDD0D 服务器状态") with mochaFlamingo
                newline()
                text("- ") with mochaSubtext0
                text("TPS：") with mochaText
                raw(getTicksPerSecond())
                newline()
                text("- ") with mochaSubtext0
                text("MSPT：") with mochaText
                raw(getMillsPerTick())
                newline()
                text("- ") with mochaSubtext0
                text("实体数：") with mochaText
                text(paper.worlds.sumOf { it.entityCount }) with mochaLavender
                newline()
                text("- ") with mochaSubtext0
                text("在线人数：") with mochaText
                text(paper.onlinePlayers.size) with mochaLavender
                newline()
                newline()
                text("» ") with mochaSubtext0
                text("ℹ 说明") with mochaBlue
                newline()
                text("- ") with mochaSubtext0
                raw(getPromptMessage())
            }
        }
    }

    private fun getTicksPerSecond(): Component {
        return StatisticProvider.getTicksPerSecond(MeasuringTime.SECONDS_10)!!.let {
            component {
                text(it.roundToTwoDecimals()) with when {
                    it in 16.0..18.0 -> mochaYellow
                    it < 16.0 -> mochaMaroon
                    else -> mochaGreen
                }
            }
        }
    }

    private fun getMillsPerTick(): Component {
        return component {
            text(
                StatisticProvider.getMillsPerTick(MeasuringTime.SECONDS_10)!!.roundToTwoDecimals()
            ) with when (StatisticProvider.getLoadLevel()!!) {
                LOW -> mochaGreen
                MODERATE -> mochaYellow
                HIGH -> mochaMaroon
            }
        }
    }

    private fun getPromptMessage(): Component {
        return component {
            when (StatisticProvider.getLoadLevel()!!) {
                LOW -> {
                    text("服务器目前负载较低，可适量开启机器、进行跑图") with mochaGreen
                }

                MODERATE -> {
                    text("服务器目前负载中等，建议关闭不在使用的机器、酌情降低跑图速度、离开村民交易所等多实体场景") with mochaYellow
                }

                HIGH -> {
                    text("服务器目前已过载，请关闭正在运行的机器、暂缓跑图、离开村民交易所等多实体场景") with mochaMaroon
                }
            }
        }
    }
}