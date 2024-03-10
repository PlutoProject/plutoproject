package ink.pmc.common.misc

import com.catppuccin.Palette
import ink.pmc.common.utils.*
import net.kyori.adventure.text.Component

val SUICIDE = Component.text("你终结了你自己...")
    .color(Palette.MOCHA.flamingo.toTextColor())

val STAND_UP = Component.text("使用 ").color(mochaText)
    .append(Component.keybind("key.sneak").color(mochaFlamingo))
    .append(Component.text(" 来站起").color(mochaText))

val ILLEGAL_LOC =
    Component.text("无法在此处坐下，请检查是否有实体方块和足够的空间").color(Palette.MOCHA.red.toTextColor())

val CHAT_FORMAT = Component.text("<player>").color(mochaYellow)
    .append(Component.text(": ").color(mochaSubtext0))
    .append(Component.text("<message>").color(mochaText))

val JOIN_FORMAT = Component.text("[+] ").color(mochaGreen)
    .append(Component.text("<player> ").color(mochaYellow))
    .append(Component.text("加入了游戏").color(mochaText))

val QUIT_FORMAT = Component.text("[-] ").color(mochaRed)
    .append(Component.text("<player> ").color(mochaYellow))
    .append(Component.text("退出了游戏").color(mochaText))

val IS_SLIMECHUNK = Component.text("你当前所在的区块是史莱姆区块").color(mochaGreen)

val ISNT_SLIMECHUNK = Component.text("你当前所在的区块不是史莱姆区块").color(mochaRed)