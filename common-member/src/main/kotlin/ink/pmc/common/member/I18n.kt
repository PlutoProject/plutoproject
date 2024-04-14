package ink.pmc.common.member

import ink.pmc.common.utils.chat.PLUTO_PROJECT
import ink.pmc.common.utils.visual.*
import net.kyori.adventure.text.Component

val MEMBER_NOT_WHITELISTED
    get() = Component.empty()
        .append(PLUTO_PROJECT)
        .appendNewline()
        .append(Component.text(" "))
        .appendNewline()
        .append(Component.text("您的游戏 ID 并没有获得白名单。").color(mochaText))
        .appendNewline()
        .append(Component.text("请加入我们的审核群 ").color(mochaText))
        .append(Component.text("795681231 ").color(mochaYellow))
        .append(Component.text("来进行申请。").color(mochaText))
        .appendNewline()
        .append(Component.text("如果您已通过申请，则可能是因为我们忘记为您添加白名单了。").color(mochaText))
        .appendNewline().append(Component.text("请联系当日的审核员来为您添加。").color(mochaText))

val MEMBER_FETCH
    get() = Component.text("正在从验证服务器查询数据...").color(mochaText)

val MEMBER_FETCH_FAILED
    get() = Component.text("信息获取失败，请检查玩家名是否正确和存在").color(mochaMaroon)

val MEMBER_FETCH_FAILED_UNKNOWN_AUTH_TYPE
    get() = Component.text("未知的验证类型，请检查是否输入正确").color(mochaMaroon)

val MEMBER_NOT_EXIST
    get() = Component.text("数据库中不存在该玩家").color(mochaMaroon)

val MEMBER_CREATE_ALREADY_EXIST
    get() = Component.text("数据库中已存在该玩家").color(mochaMaroon)

val MEMBER_CREATE_SUCCEED
    get() = Component.text("已成功添加名为 <player> 的玩家").color(mochaGreen)

val MEMBER_CREATE_BE_ALREADY_EXISTED
    get() = Component.text("无法创建新的仅基岩版玩家 <player>，该基岩版账号 <gamertag> (XUID: <xuid>) 已经被玩家 <other> 绑定了")
        .color(mochaMaroon)

val MEMBER_MODIFY_EXEMPT_WHITELIST_SUCCEED
    get() = Component.text("已成功移除玩家 <player> 的白名单").color(mochaGreen)

val MEMBER_MODIFY_EXEMPT_WHITELIST_FAILED_NOT_WHITELISTED
    get() = Component.text("玩家 <player> 未获得白名单").color(mochaMaroon)

val MEMBER_MODIFY_GRAND_WHITELIST_SUCCEED
    get() = Component.text("已成功为玩家 <player> 发放白名单").color(mochaGreen)

val MEMBER_MODIFY_GRANT_WHITELIST_FAILED_ALREADY_WHITELISTED
    get() = Component.text("玩家 <player> 已获得白名单").color(mochaMaroon)

val MEMBER_MODIFY_LINK_BE_FAILED_BE_ONLY
    get() = Component.text("该玩家是仅基岩版进入的玩家，无法进行绑定").color(mochaMaroon)
        .appendNewline()
        .append(Component.text("如果你执意要这么做，请加上 ").color(mochaSubtext0))
        .append(Component.text("--force ").color(mochaFlamingo))
        .append(Component.text("标签").color(mochaSubtext0))

val MEMBER_MODIFY_LINK_BE_FAILED_ACCOUNT_ALREADY_EXISTED
    get() = Component.text("绑定失败，账号 <gamertag> (XUID: <xuid>) 已经被玩家 <other> 绑定了").color(mochaMaroon)

val MEMBER_MODIFY_LINK_BE_FAILED_ALREADY_LINKED
    get() = Component.text("绑定失败，该玩家已经绑定过基岩版账号").color(mochaMaroon)

val MEMBER_MODIFY_LINK_BE_FAILED_NOT_EXISTED
    get() = Component.text("绑定失败，该基岩版账号不存在")

val MEMBER_MODIFY_LINK_BE_SUCCEED
    get() = Component.text("已成功为玩家 <player> 绑定基岩版账号").color(mochaGreen)

val MEMBER_MODIFY_UNLINK_BE_FAILED_ALREADY_BE_ONLY
    get() = Component.text("该是仅基岩版进入的玩家，无法进行解绑").color(mochaMaroon)
        .appendNewline()
        .append(Component.text("如果你执意要这么做，请加上 ").color(mochaSubtext0))
        .append(Component.text("--force ").color(mochaFlamingo))
        .append(Component.text("标签").color(mochaSubtext0))

val MEMBER_MODIFY_UNLINK_BE_FAILED_NOT_LINKED
    get() = Component.text("解绑失败，该玩家未绑定基岩版账号").color(mochaMaroon)

val MEMBER_MODIFY_UNLINK_BE_SUCCEED
    get() = Component.text("已成功为玩家 <player> 解绑基岩版账号").color(mochaGreen)

val MEMBER_LOOKUP
    get() = Component.text("UID：").color(mochaText).append(Component.text("<uid>").color(mochaYellow))
        .appendNewline()
        .append(Component.text("UUID: ").color(mochaText).append(Component.text("<id>").color(mochaYellow)))
        .appendNewline()
        .append(Component.text("玩家名：").color(mochaText).append(Component.text("<name>").color(mochaYellow)))
        .appendNewline()
        .append(Component.text("格式化玩家名：").color(mochaText).append(Component.text("<rawName>").color(mochaYellow)))
        .appendNewline()
        .append(Component.text("白名单状态：").color(mochaText))
        .append(Component.text("<whitelistStatus>").color(mochaYellow))
        .appendNewline()
        .append(Component.text("验证类型：").color(mochaText).append(Component.text("<authType>").color(mochaYellow)))
        .appendNewline()
        .append(Component.text("创建时间：").color(mochaText).append(Component.text("<createdAt>").color(mochaYellow)))
        .appendNewline()
        .append(Component.text("上次进入时间：").color(mochaText))
        .append(Component.text("<lastJoinedAt>").color(mochaYellow))
        .appendNewline()
        .append(Component.text("上次退出时间：").color(mochaText))
        .append(Component.text("<lastQuitedAt>").color(mochaYellow))
        .appendNewline()
        .append(Component.text("数据容器：").color(mochaText))
        .append(Component.text("<dataContainer>").color(mochaYellow))
        .appendNewline()
        .append(Component.text("基岩版账号：").color(mochaText))
        .append(Component.text("<bedrockAccount>").color(mochaYellow))
        .appendNewline()
        .append(Component.text("个性签名：").color(mochaText).append(Component.text("<bio>").color(mochaYellow)))