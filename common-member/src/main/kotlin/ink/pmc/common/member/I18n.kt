package ink.pmc.common.member

import ink.pmc.common.utils.mochaGreen
import ink.pmc.common.utils.mochaMaroon
import ink.pmc.common.utils.mochaText
import net.kyori.adventure.text.Component

val LOOKUP = Component.text("正在从 Mojang 服务器查询信息...").color(mochaText)

val LOOKUP_FAILED = Component.text("信息获取失败，请检查玩家名是否正确和存在").color(mochaMaroon)

val MEMBER_ALREADY_EXIST = Component.text("该玩家已存在").color(mochaMaroon)

val MEMBER_ADD_SUCCEED = Component.text("已成功添加名为 <player> 的成员").color(mochaGreen)