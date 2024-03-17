package ink.pmc.common.member

import ink.pmc.common.utils.visual.*
import net.kyori.adventure.text.Component

val NOT_WHITELISTED = Component.text("星社 ").color(mochaMauve)
    .append(Component.text("Project").color(mochaText))
    .appendNewline().append(Component.text(" "))
    .appendNewline().append(Component.text("您的游戏 ID 并没有获得白名单。").color(mochaText))
    .appendNewline().append(
        Component.text("请加入我们的审核群 ").color(mochaText).append(
            Component.text("795681231 ").color(mochaYellow)
                .append(Component.text("来进行申请。").color(mochaText))
        )
    )
    .appendNewline().append(
        Component.text("如果您已通过申请，则可能是因为我们忘记为您添加白名单了。").color(
            mochaText
        )
    )
    .appendNewline().append(Component.text("请联系当日的审核员来为您添加。").color(mochaText))

val LOOKUP = Component.text("正在从 Mojang 服务器查询信息...").color(mochaText)

val LOOKUP_FAILED = Component.text("信息获取失败，请检查玩家名是否正确和存在").color(mochaMaroon)

val MEMBER_ALREADY_EXIST = Component.text("该成员已存在").color(mochaMaroon)

val MEMBER_ADD_SUCCEED = Component.text("已成功添加名为 <player> 的成员").color(mochaGreen)

val MEMBER_NOT_EXIST = Component.text("该成员不存在").color(mochaMaroon)

val MEMBER_LOOKUP = Component.text("玩家名：").color(mochaText).append(Component.text("<player>").color(mochaYellow))
    .appendNewline()
    .append(Component.text("UUID: ").color(mochaText).append(Component.text("<uuid>").color(mochaYellow)))
    .appendNewline()
    .append(Component.text("个性签名：").color(mochaText).append(Component.text("<bio>").color(mochaYellow)))
    .appendNewline()
    .append(Component.text("加入时间：").color(mochaText).append(Component.text("<joinTime>").color(mochaYellow)))
    .appendNewline().append(
        Component.text("上次进入时间：").color(mochaText).append(Component.text("<lastJoinTime>").color(mochaYellow))
    )
    .appendNewline().append(
        Component.text("上次退出时间：").color(mochaText).append(Component.text("<lastQuitTime>").color(mochaYellow))
    )
    .appendNewline()
    .append(Component.text("总游玩时间：").color(mochaText).append(Component.text("<totalPlayTime>").color(mochaYellow)))
    .appendNewline()
    .append(Component.text("数据组件：").color(mochaText).append(Component.text("<data>").color(mochaYellow)))