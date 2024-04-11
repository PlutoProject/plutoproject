package ink.pmc.common.member.api

import ink.pmc.common.utils.visual.mochaMaroon
import ink.pmc.common.utils.visual.mochaPeach
import ink.pmc.common.utils.visual.mochaYellow
import net.kyori.adventure.text.Component

@Suppress("UNUSED")
val BAN_DISPLAY
    get() = Component.text("封禁").color(mochaMaroon)

@Suppress("UNUSED")
val WARN_A_DISPLAY
    get() = Component.text("A 类警告").color(mochaPeach)

@Suppress("UNUSED")
val WARN_B_DISPLAY
    get() = Component.text("B 类警告").color(mochaYellow)

val STATUS_WHITELISTED
    get() = Component.text("已发放")

val STATUS_NON_WHITELISTED
    get() = Component.text("未发放")

val STATUS_WHITELISTED_BEFORE
    get() = Component.text("曾发放过，但被移除了")

val OFFICIAL_AUTH
    get() = Component.text("正版账号")

val LITTLESKIN_AUTH
    get() = Component.text("LittleSkin 皮肤站")

val BEDROCK_ONLY_AUTH
    get() = Component.text("仅基岩版")

val NONE_AUTH
    get() = Component.text("无")

val BAN_PUNISHMENT
    get() = Component.text("封禁")

val WARN_PUNISHMENT
    get() = Component.text("警告")

val REMOVE_WHITELIST_PUNISHMENT
    get() = Component.text("撤销白名单")