package ink.pmc.utils.chat

import ink.pmc.advkt.component.component
import ink.pmc.advkt.component.miniMessage
import ink.pmc.advkt.component.newline
import ink.pmc.advkt.component.text
import ink.pmc.utils.visual.mochaMaroon
import ink.pmc.utils.visual.mochaSubtext0

val NON_PLAYER = component { text("该命令仅限玩家使用") with mochaMaroon }

val EMPTY_LINE = component { }

val PLUTO_PROJECT = component { miniMessage("<gradient:#c6a0f6:#f5bde6:#f0c6c6:#f4dbd6>星社 ᴘʀᴏᴊᴇᴄᴛ</gradient>") }

val PLAYER_NOT_ONLINE = component { text("该玩家不在线") with mochaMaroon }

val NO_PERMISSON = component {
    text("你似乎没有权限这么做") with mochaMaroon
    newline()
    text("如果你认为这是一个错误的话，请向管理组报告") with mochaSubtext0
}