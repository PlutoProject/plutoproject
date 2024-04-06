package ink.pmc.common.member

import ink.pmc.common.utils.visual.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration

val NOT_WHITELISTED
    get() = Component.text("星社 ").color(mochaMauve)
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

val LOOKUP
    get() = Component.text("正在从 Mojang 服务器查询信息...").color(mochaText)

val LOOKUP_FAILED
    get() = Component.text("信息获取失败，请检查玩家名是否正确和存在").color(mochaMaroon)

val MEMBER_ALREADY_EXIST
    get() = Component.text("数据库中已存在该玩家").color(mochaMaroon)

val MEMBER_ADD_SUCCEED
    get() = Component.text("已成功添加名为 <player> 的玩家").color(mochaGreen)

val MEMBER_REMOVE_FAILED_ONLINE
    get() = Component.text("该玩家仍然在线，请将其踢出后再试").color(mochaMaroon)

val MEMBER_REMOVE_SUCCEED
    get() = Component.text("已成功移除名为 <player> 的玩家").color(mochaGreen)
        .appendNewline().append(
            Component.text("该操作可能需要一段时间才可以同步到后端服务器").decorate(TextDecoration.ITALIC).color(
                mochaSubtext1
            )
        )

val MEMBER_NOT_EXIST
    get() = Component.text("数据库中不存在该玩家").color(mochaMaroon)

val MEMBER_LOOKUP
    get() = Component.text("玩家名：").color(mochaText).append(Component.text("<player>").color(mochaYellow))
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
        .append(
            Component.text("总游玩时间：").color(mochaText).append(Component.text("<totalPlayTime>").color(mochaYellow))
        )
        .appendNewline()
        .append(Component.text("数据组件：").color(mochaText).append(Component.text("<data>").color(mochaYellow)))

val ID_NOT_EXIST
    get() = Component.text("ID 为 <player> 的玩家未找到").color(mochaMaroon)

val ID_ADDED
    get() = Component.text("ID 为 <player> 的玩家添加成功").color(mochaGreen)

val TIME_UPDATED
    get() = Component.text("ID 为 <player> 的玩家时间更新成功").color(mochaGreen)

val TIME_UPDATED_FAILED
    get() = Component.text("ID 为 <player> 的玩家时间更新失败").color(mochaMaroon)

val WAITING_API
    get() = Component.text("等待 60s 再继续操作以防 API 限制").color(mochaGreen)

val OPERATION_FINISHED
    get() = Component.text("操作已全部完成").color(mochaGreen)