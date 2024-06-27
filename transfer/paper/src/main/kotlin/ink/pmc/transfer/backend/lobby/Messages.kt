package ink.pmc.transfer.backend.lobby

import ink.pmc.advkt.component.text
import ink.pmc.advkt.title.mainTitle
import ink.pmc.advkt.title.title
import ink.pmc.utils.visual.mochaMauve
import ink.pmc.utils.visual.mochaRed

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
            text("踏入传送门来选择服务器 $RANDOM_KAOMOJI") with mochaRed
        }
    }

val PLAYER_JOIN_WHITELISTED
    get() = title {
        mainTitle {
            text("欢迎回来") with mochaMauve
            text("踏入传送门来选择服务器 $RANDOM_KAOMOJI") with mochaRed
        }
    }

val PLAYER_JOIN_NOT_WHITELISTED
    get() = title {
        mainTitle {
            text("很高兴见到你！") with mochaMauve
            text("查阅前方信息来了解白名单事宜 $RANDOM_KAOMOJI") with mochaRed
        }
    }