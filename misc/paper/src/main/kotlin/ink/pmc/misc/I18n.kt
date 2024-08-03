package ink.pmc.misc

import ink.pmc.advkt.component.*
import ink.pmc.advkt.sound.*
import ink.pmc.advkt.title.*
import ink.pmc.utils.visual.*
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.title.Title
import kotlin.time.Duration.Companion.seconds

val SUICIDE = component {
    text("你结束了自己的生命...") with mochaFlamingo
}

@Suppress("UNUSED")
val STAND_UP = component {
    text("使用 ") with mochaPink
    keybind("key.sneak") with mochaText
    text(" 键起身") with mochaPink
}

val STAND_UP_BE = component {
    text("按下潜行键起身") with mochaPink
}

val ILLEGAL_LOC = component {
    text("无法在此处坐下，请检查是否有实体方块以及足够的空间") with mochaMaroon
}

val MULTI_SITTERS_TITLE = title {
    mainTitle {
        text(" ")
    }
    subTitle {
        text("此位置已有其他人坐下") with mochaMaroon
    }
    times {
        fadeIn(0.seconds)
        stay(1.seconds)
        fadeOut(0.seconds)
    }
}

val MULTI_SITTERS_SOUND = sound {
    key(Key.key("block.note_block.hat"))
    source(Sound.Source.BLOCK)
    volume(1f)
    pitch(1f)
}

val CHAT_FORMAT = component {
    text("<player>") with mochaFlamingo
    text(": ") with mochaSubtext0
    text("<message>") with mochaText
}

val JOIN_FORMAT = component {
    text("[+] ") with mochaGreen
    text("<player> ") with mochaFlamingo
    text("加入了游戏") with mochaPink
}

val QUIT_FORMAT = component {
    text("[-] ") with mochaRed
    text("<player> ") with mochaFlamingo
    text("退出了游戏") with mochaPink
}

fun elevatorGoUpTitle(curr: Int, total: Int): Title {
    return title {
        mainTitle {
            text(" ")
        }
        subTitle {
            text("电梯上行 ") with mochaYellow
            text("($curr/$total)") with mochaSubtext0
        }
        times {
            fadeIn(0.seconds)
            stay(1.seconds)
            fadeOut(0.seconds)
        }
    }
}

fun elevatorGoDownTitle(curr: Int, total: Int): Title {
    return title {
        mainTitle {
            text(" ")
        }
        subTitle {
            text("电梯下行 ") with mochaYellow
            text("($curr/$total)") with mochaSubtext0
        }
        times {
            fadeIn(0.seconds)
            stay(1.seconds)
            fadeOut(0.seconds)
        }
    }
}

val ELEVATOR_WORK_SOUND = sound {
    key(Key.key("entity.iron_golem.attack"))
    source(Sound.Source.BLOCK)
    volume(1f)
    pitch(1f)
}

val HEAD_GET_LOAD_DATA = component {
    text("请稍等，正在获取数据") with mochaFlamingo
}

val HEAD_GET_SUCCEED = component {
    text("已成功获取正版玩家 ") with mochaPink
    text("<player> ") with mochaFlamingo
    text("的头颅") with mochaPink
    newline()
    text("此功能目前正处于测试阶段，请勿滥用") with mochaSubtext0
}

val HEAD_GET_FAILED = component {
    text("查询失败，请检查输入的玩家名是否正确") with mochaMaroon
    newline()
    text("若玩家名无误，可能是查询接口限额或无法访问，请稍后再试") with mochaSubtext0
}

val HEAD_GET_FAILED_INV_FULL = component {
    text("背包已满，请腾出空间后再试") with mochaSubtext0
}
