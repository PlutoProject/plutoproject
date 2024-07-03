package ink.pmc.transfer.backend.lobby

import ink.pmc.advkt.component.component
import ink.pmc.advkt.component.italic
import ink.pmc.advkt.component.text
import ink.pmc.advkt.title.*
import ink.pmc.transfer.api.Destination
import ink.pmc.transfer.api.DestinationStatus
import ink.pmc.utils.visual.*
import net.kyori.adventure.text.Component
import kotlin.time.Duration.Companion.seconds

val KAOMOJI_ARRAY = arrayOf(
    "ヽ( ° ▽°)ノ",
    "(｢･ω･)｢",
    "╰(*°▽°*)╯",
    "ヾ(´︶`*)ﾉ♬",
    "( ～'ω')～",
    "(*´∀`)~♥",
    "(￣▽￣)/",
    "( ^ω^)",
    "(๑¯∀¯๑)",
    "(〃´∀｀)"
)

val RANDOM_KAOMOJI
    get() = KAOMOJI_ARRAY.random()

val PLAYER_JOIN_WHITELISTED_FIRST
    get() = title {
        mainTitle {
            text("很高兴见到你！") with mochaMauve
        }
        subTitle {
            text("踏入传送门来选择服务器 $RANDOM_KAOMOJI") with mochaRed
        }
    }

val PLAYER_JOIN_WHITELISTED
    get() = title {
        mainTitle {
            text("欢迎回来") with mochaMauve
        }
        subTitle {
            text("踏入传送门来选择服务器 $RANDOM_KAOMOJI") with mochaRed
        }
    }

val PLAYER_JOIN_NOT_WHITELISTED
    get() = title {
        mainTitle {
            text("很高兴见到你！") with mochaMauve
        }
        subTitle {
            text("查阅前方信息来了解白名单事宜 $RANDOM_KAOMOJI") with mochaRed
        }
    }

val MAIN_MENU_TITLE = arrayOf(
    "去往何处？",
    "下一个目标是？"
)

val RANDOM_MAIN_MENU_TITLE
    get() = MAIN_MENU_TITLE.random()

val MENU_CLOSE = component {
    text("关闭") without italic() with mochaMaroon
}

val MENU_BACK = component {
    text("关闭") without italic() with mochaYellow
}

fun destinationStatus(destination: Destination): Component {
    return when(destination.status) {
        DestinationStatus.ONLINE -> {
            component {
                text("· 在线 ") without italic() with mochaGreen
                text("${destination.playerCount}/${destination.maxPlayerCount}") without italic() with mochaText
            }
        }
        DestinationStatus.OFFLINE -> {
            component {
                text("· 服务器离线") without italic() with mochaMaroon
            }
        }
        DestinationStatus.MAINTENANCE -> {
            component {
                text("· 服务器离线 ") without italic() with mochaMaroon
                text("维护中") without italic() with mochaSubtext0
            }
        }
    }
}

val DESTINATION_NOT_AVAILABLE = component {
    text("× 不兼容的版本") without italic() with mochaMaroon
}

val DESTINATION_CLICK_TO_JOIN = component {
    text("√ 点击以加入") without italic() with mochaFlamingo
}

val DESTINATION_TEMP_CANT_JOIN = component {
    text("× 暂时无法加入") without italic() with mochaMaroon
}

fun destinationJoinPrompt(destination: Destination, condition: Boolean, error: Component?): Component {
    when(destination.status) {
        DestinationStatus.OFFLINE -> { return DESTINATION_TEMP_CANT_JOIN }
        DestinationStatus.MAINTENANCE -> { return DESTINATION_TEMP_CANT_JOIN }
        else -> {}
    }

    if (!condition) {
        return error ?: DESTINATION_NOT_AVAILABLE
    }

    return DESTINATION_CLICK_TO_JOIN
}

val CATEGORY_CLICK_TO_OPEN = component {
    text("√ 点击打开") without italic() with mochaFlamingo
}

val LOBBY_TRANSFER_PREPARE_TITLE = title {
    times {
        fadeIn(0.3.seconds)
        stay(10.seconds)
        fadeOut(0.seconds)
    }
    mainTitle {
        text("传送中") with mochaPink
    }
    subTitle {
        text("请稍作等候") with mochaText
    }
}

val LOBBY_TRANSFER_FAILED_TITLE = title {
    times {
        fadeIn(0.3.seconds)
        stay(3.seconds)
        fadeOut(0.3.seconds)
    }
    mainTitle {
        text("传送失败") with mochaMaroon
    }
    subTitle {
        text("请再试一次吧") with mochaSubtext0
    }
}