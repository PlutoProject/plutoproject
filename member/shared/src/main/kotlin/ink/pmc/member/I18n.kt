package ink.pmc.member

import ink.pmc.advkt.component.component
import ink.pmc.advkt.component.newline
import ink.pmc.advkt.component.raw
import ink.pmc.advkt.component.text
import ink.pmc.utils.bedrock.bedrockMapped
import ink.pmc.utils.chat.PLUTO_PROJECT
import ink.pmc.utils.visual.*

val MEMBER_NOT_WHITELISTED = component { 
    raw(PLUTO_PROJECT)
    newline()
    text(" ")
    newline()
    text("")
    text("您的游戏 ID 并没有获得白名单。") with mochaText
    newline()
    text("请加入我们的审核群 ") with mochaText
    text("152759828 ") with mochaYellow
    text("来进行申请。") with mochaText
    newline()
    text("如果您已通过申请，则可能是因为我们忘记为您添加白名单了。") with mochaText
    newline()
    text("请联系当日的审核员来为您添加。") with mochaText
}

val MEMBER_NOT_WHITELISTED_BE = component {
    text("您的游戏 ID 并没有获得白名单。") with mochaText.bedrockMapped
    newline()
    text("请加入我们的审核群 ") with mochaText.bedrockMapped
    text("795681231 ") with mochaYellow.bedrockMapped
    text("来进行申请。") with mochaText.bedrockMapped
    newline()
    text("如果您已通过申请，则可能是因为我们忘记为您添加白名单了。") with mochaText.bedrockMapped
    newline()
    text("请联系当日的审核员来为您添加。") with mochaText.bedrockMapped
}

@Suppress("UNUSED")
val MEMBER_BE_LOGIN = component {
    raw(PLUTO_PROJECT)
    newline()
    text(" ")
    newline()
    text("您绑定的基岩版账号在其他地方登录了。") with mochaText
    newline()
    text("绑定后，您的基岩版账号和 Java 版账号将会互通数据，因此无法同时登录。") with mochaText
    newline()
    text("若您想让基岩版账号作为一个单独的账号，请向服务器管理组申请解绑。") with mochaText
}

val MEMBER_NAME_CHANGED = component {
    text("检测到您的玩家名从 <oldName> 变更为 <newName>，已自动更新数据库内的数据") with mochaGreen
}

val MEMBER_FETCH = component {
    text("正在从验证服务器查询数据...") with mochaText
}

val MEMBER_FETCH_FAILED = component {
    text("信息获取失败，请检查玩家名是否正确和存在") with mochaMaroon
}

val MEMBER_FETCH_FAILED_UNKNOWN_AUTH_TYPE = component {
    text("未知的验证类型，请检查是否输入正确") with mochaMaroon
}

val MEMBER_NOT_EXIST = component {
    text("数据库中不存在该玩家") with mochaMaroon
}

val MEMBER_CREATE_ALREADY_EXIST = component {
    text("数据库中已存在该玩家") with mochaMaroon
}

val MEMBER_CREATE_SUCCEED = component {
    text("已成功添加名为 <player> 的玩家") with mochaGreen
}

val MEMBER_CREATE_BE_ALREADY_EXISTED = component {
    text("无法创建新的仅基岩版玩家 <player>，该基岩版账号 <gamertag> (XUID: <xuid>) 已经被玩家 <other> 绑定了") with mochaMaroon
}

val MEMBER_MODIFY_EXEMPT_WHITELIST_SUCCEED = component {
    text("已成功移除玩家 <player> 的白名单") with mochaGreen
}

val MEMBER_MODIFY_EXEMPT_WHITELIST_FAILED_NOT_WHITELISTED = component {
    text("玩家 <player> 未获得白名单") with mochaMaroon
}

val MEMBER_MODIFY_GRAND_WHITELIST_SUCCEED = component {
    text("已成功为玩家 <player> 发放白名单") with mochaGreen
}

val MEMBER_MODIFY_GRANT_WHITELIST_FAILED_ALREADY_WHITELISTED = component {
    text("玩家 <player> 已获得白名单") with mochaMaroon
}

val MEMBER_MODIFY_LINK_BE_FAILED_BE_ONLY = component {
    text("该玩家是仅基岩版进入的玩家，无法进行绑定") with mochaMaroon
    newline()
    text("如果你执意要这么做，请加上 ") with mochaSubtext0
    text("--force ") with mochaFlamingo
    text("标签") with mochaSubtext0
}

val MEMBER_MODIFY_LINK_BE_FAILED_ACCOUNT_ALREADY_EXISTED = component {
    text("绑定失败，账号 <gamertag> (XUID: <xuid>) 已经被玩家 <other> 绑定了") with mochaMaroon
}

val MEMBER_MODIFY_LINK_BE_FAILED_ALREADY_LINKED = component {
    text("绑定失败，该玩家已经绑定过基岩版账号") with mochaMaroon
}

val MEMBER_MODIFY_LINK_BE_FAILED_NOT_EXISTED = component {
    text("绑定失败，该基岩版账号不存在") with mochaMaroon
}

val MEMBER_MODIFY_LINK_BE_SUCCEED = component {
    text("已成功为玩家 <player> 绑定基岩版账号") with mochaGreen
}

val MEMBER_MODIFY_UNLINK_BE_FAILED_ALREADY_BE_ONLY = component {
    text("该玩家是仅基岩版进入的玩家，无法进行解绑") with mochaMaroon
    newline()
    text("如果你执意要这么做，请加上 ") with mochaSubtext0
    text("--force ") with mochaFlamingo
    text("标签") with mochaSubtext0
}

val MEMBER_MODIFY_UNLINK_BE_FAILED_NOT_LINKED = component {
    text("解绑失败，该玩家未绑定基岩版账号") with mochaMaroon
}

val MEMBER_MODIFY_UNLINK_BE_KICK = component {
    text("您的基岩版账号已经被解绑，请使用 Java 版账号游玩。") with mochaMaroon.bedrockMapped
}

val MEMBER_MODIFY_UNLINK_BE_SUCCEED = component {
    text("已成功为玩家 <player> 解绑基岩版账号") with mochaGreen
}

val MEMBER_MODIFY_HIDE_SUCCEED = component {
    text("已成功隐藏玩家 <player>") with mochaGreen
}

val MEMBER_MODIFY_UN_HIDE_SUCCEED = component {
    text("已成功取消玩家 <player> 的隐藏状态") with mochaGreen
}

val MEMBER_MODIFY_HIDE_FAILED = component {
    text("玩家 <player> 本身就在隐藏状态中") with mochaMaroon
}

val MEMBER_MODIFY_UN_HIDE_FAILED = component {
    text("玩家 <player> 本身就不在隐藏状态中") with mochaMaroon
}

val MEMBER_LOOKUP = component {
    text("UID：") with mochaText
    text("<uid>") with mochaYellow
    newline()
    text("UUID: ") with mochaText
    text("<id>") with mochaYellow
    newline()
    text("玩家名：") with mochaText
    text("<name>") with mochaYellow
    newline()
    text("格式化玩家名：") with mochaText
    text("<rawName>") with mochaYellow
    newline()
    text("白名单状态：") with mochaText
    text("<whitelistStatus>") with mochaYellow
    newline()
    text("验证类型：") with mochaText
    text("<authType>") with mochaYellow
    newline()
    text("创建时间：") with mochaText
    text("<createdAt>") with mochaYellow
    newline()
    text("上次进入时间：") with mochaText
    text("<lastJoinedAt>") with mochaYellow
    newline()
    text("上次退出时间：") with mochaText
    text("<lastQuitedAt>") with mochaYellow
    newline()
    text("数据容器：") with mochaText
    text("<dataContainer>") with mochaYellow
    newline()
    text("基岩版账号：") with mochaText
    text("<bedrockAccount>") with mochaYellow
    newline()
    text("个性签名：") with mochaText
    text("<bio>") with mochaYellow
    newline()
    text("是否隐藏：") with mochaText
    text("<isHidden>") with mochaYellow
}