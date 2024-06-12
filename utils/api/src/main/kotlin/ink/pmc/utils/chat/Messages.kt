package ink.pmc.utils.chat

import ink.pmc.advkt.component.component
import ink.pmc.advkt.component.miniMessage
import ink.pmc.advkt.component.text
import ink.pmc.utils.visual.mochaMaroon

val NON_PLAYER = component { text("该命令仅限玩家使用") with mochaMaroon }

val EMPTY_LINE = component { }

val PLUTO_PROJECT = component { miniMessage("<gradient:#c6a0f6:#f5bde6:#f0c6c6:#f4dbd6>星社 ᴘʀᴏᴊᴇᴄᴛ</gradient>") }

val PLAYER_NOT_ONLINE = component { text("该玩家不在线") with mochaMaroon }