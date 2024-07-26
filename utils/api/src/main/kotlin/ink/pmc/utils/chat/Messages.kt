package ink.pmc.utils.chat

import ink.pmc.advkt.component.component
import ink.pmc.advkt.component.miniMessage
import ink.pmc.advkt.component.newline
import ink.pmc.advkt.component.text
import ink.pmc.utils.visual.mochaMaroon
import ink.pmc.utils.visual.mochaSubtext0
import ink.pmc.utils.visual.mochaText
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import kotlin.time.Duration
import kotlin.time.toJavaDuration

val NON_PLAYER = component { text("该命令仅限玩家使用") with mochaMaroon }

val EMPTY_LINE = component { }

val PLUTO_PROJECT = component { miniMessage("<gradient:#c6a0f6:#f5bde6:#f0c6c6:#f4dbd6>星社 ᴘʀᴏᴊᴇᴄᴛ</gradient>") }

val PLAYER_NOT_ONLINE = component { text("该玩家不在线") with mochaMaroon }

val NO_PERMISSON = component {
    text("你似乎没有权限这么做") with mochaMaroon
    newline()
    text("如果你认为这是一个错误的话，请向管理组报告") with mochaSubtext0
}

@Suppress("FunctionName")
fun DURATION(
    duration: Duration,
    numberColor: TextColor = mochaText,
    textColor: TextColor = mochaSubtext0
): Component {
    val java = duration.toJavaDuration()
    val component = Component.empty()

    val days = java.toDaysPart()
    val hours = java.toHoursPart()
    val minutes = java.toMinutesPart()
    val seconds = java.toSecondsPart()
    val ms = java.toMillisPart()
    val nanos = java.toNanosPart()

    if (days != 0L) {
        component.append(component {
            text("$days ") with numberColor
            text("天") with textColor
        })
    }

    if (hours != 0) {
        component.append(component {
            text("$hours ") with numberColor
            text("小时") with textColor
        })
    }

    if (minutes != 0) {
        component.append(component {
            text("$minutes ") with numberColor
            text("分钟") with textColor
        })
    }

    if (seconds != 0) {
        component.append(component {
            text("$seconds ") with numberColor
            text("秒") with textColor
        })
    }

    if (ms != 0) {
        component.append(component {
            text("$ms ") with numberColor
            text("毫秒") with textColor
        })
    }

    if (nanos != 0) {
        component.append(component {
            text("$nanos ") with numberColor
            text("纳秒") with textColor
        })
    }

    return component
}