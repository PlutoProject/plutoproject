package ink.pmc.common.utils.chat

import ink.pmc.common.utils.visual.mochaMaroon
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage

val NON_PLAYER
    get() = Component.text("该命令仅限玩家使用").color(mochaMaroon)

val EMPTY_LINE
    get() = Component.text(" ")

val PLUTO_PROJECT =
    MiniMessage.miniMessage().deserialize("<gradient:#c6a0f6:#f5bde6:#f0c6c6:#f4dbd6>星社 ᴘʀᴏᴊᴇᴄᴛ</gradient>")

val PLAYER_NOT_ONLINE
    get() = Component.text("该玩家不在线").color(mochaMaroon)