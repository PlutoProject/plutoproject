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
    text("你的游戏 ID 未获得白名单") with mochaText
    newline()
    text("请加入我们的审核群 ") with mochaText
    text("152759828 ") with mochaYellow
    text("进行申请") with mochaText
    newline()
    text("如果你已通过申请，可能是我们忘记为你添加白名单") with mochaText
    newline()
    text("请联系当天的审核员添加白名单") with mochaText
}

val MEMBER_NOT_WHITELISTED_BE = component {
    text("你的游戏 ID 未获得白名单") with mochaText.bedrockMapped
    newline()
    text("请加入我们的审核群 ") with mochaText.bedrockMapped
    text("795681231 ") with mochaYellow.bedrockMapped
    text("进行申请") with mochaText.bedrockMapped
    newline()
    text("如果你已通过申请，可能是我们忘记为你添加白名单") with mochaText.bedrockMapped
    newline()
    text("请联系当天的审核员添加白名单") with mochaText.bedrockMapped
}

@Suppress("UNUSED")
val MEMBER_BE_LOGIN = component {
    raw(PLUTO_PROJECT)
    newline()
    text(" ")
    newline()
    text("你绑定的基岩版账号在其他地方登录") with mochaText
    newline()
    text("绑定后，基岩版和 Java 版账号数据互通，无法同时登录") with mochaText
    newline()
    text("若需基岩版账号独立使用，请向管理组申请解绑") with mochaText
}

val MEMBER_NAME_CHANGED = component {
    text("检测到你的玩家名已从 ") with mochaPink
    text("<oldName> ") with mochaFlamingo
    text("更改为 ") with mochaPink
    text("<newName>") with mochaFlamingo
    text("，数据库已自动更新") with mochaPink
}

val MEMBER_FETCH = component {
    text("正在从验证服务器查询数据...") with mochaText
}

val MEMBER_FETCH_FAILED = component {
    text("信息获取失败，请检查玩家名是否正确且存在") with mochaMaroon
}

val MEMBER_FETCH_FAILED_UNKNOWN_AUTH_TYPE = component {
    text("未知的验证类型，请检查输入是否正确") with mochaMaroon
}

val MEMBER_NOT_EXIST = component {
    text("数据库中不存在该玩家") with mochaMaroon
}

val MEMBER_CREATE_ALREADY_EXIST = component {
    text("数据库中已存在该玩家") with mochaMaroon
}

val MEMBER_CREATE_SUCCEED = component {
    text("已成功添加名为 ") with mochaPink
    text("<player> ") with mochaFlamingo
    text("的玩家") with mochaPink
}

val MEMBER_CREATE_BE_ALREADY_EXISTED = component {
    text("无法创建仅基岩版玩家 ") with mochaMaroon
    text("<player>") with mochaFlamingo
    text("，该基岩版账号 ") with mochaMaroon
    text("<gamertag> ") with mochaFlamingo
    text("(XUID: <xuid>) ") with mochaSubtext0
    text("已被玩家 ") with mochaMaroon
    text("<other> ") with mochaFlamingo
    text("绑定") with mochaMaroon
}

val MEMBER_MODIFY_EXEMPT_WHITELIST_SUCCEED = component {
    text("已成功移除玩家 ") with mochaPink
    text("<player> ") with mochaFlamingo
    text("的白名单") with mochaPink
}

val MEMBER_MODIFY_EXEMPT_WHITELIST_FAILED_NOT_WHITELISTED = component {
    text("玩家 ") with mochaMaroon
    text("<player> ") with mochaFlamingo
    text("未获得白名单") with mochaMaroon
}

val MEMBER_MODIFY_GRANT_WHITELIST_SUCCEED = component {
    text("已成功为玩家 ") with mochaPink
    text("<player> ") with mochaFlamingo
    text("发放白名单") with mochaPink
}

val MEMBER_MODIFY_GRANT_WHITELIST_FAILED_ALREADY_WHITELISTED = component {
    text("玩家 <player> 已获得白名单") with mochaMaroon
}

val MEMBER_MODIFY_LINK_BE_FAILED_BE_ONLY = component {
    text("该玩家为仅基岩版玩家，无法绑定") with mochaMaroon
    newline()
    text("如需继续操作，请加上 ") with mochaSubtext0
    text("--force ") with mochaFlamingo
    text("标签") with mochaSubtext0
}

val MEMBER_MODIFY_LINK_BE_FAILED_ACCOUNT_ALREADY_EXISTED = component {
    text("绑定失败，账号 <gamertag> (XUID: <xuid>) 已被玩家 <other> 绑定") with mochaMaroon
}

val MEMBER_MODIFY_LINK_BE_FAILED_ALREADY_LINKED = component {
    text("绑定失败，该玩家已绑定基岩版账号") with mochaMaroon
}

val MEMBER_MODIFY_LINK_BE_FAILED_NOT_EXISTED = component {
    text("绑定失败，该基岩版账号不存在") with mochaMaroon
}

val MEMBER_MODIFY_LINK_BE_SUCCEED = component {
    text("已成功为玩家 <player> 绑定基岩版账号") with mochaGreen
}

val MEMBER_MODIFY_UNLINK_BE_FAILED_ALREADY_BE_ONLY = component {
    text("该玩家为仅基岩版玩家，无法解绑") with mochaMaroon
    newline()
    text("如需继续操作，请加上 ") with mochaSubtext0
    text("--force ") with mochaFlamingo
    text("标签") with mochaSubtext0
}

val MEMBER_MODIFY_UNLINK_BE_FAILED_NOT_LINKED = component {
    text("解绑失败，该玩家未绑定基岩版账号") with mochaMaroon
}

val MEMBER_MODIFY_UNLINK_BE_KICK = component {
    text("你的基岩版账号已解绑，请使用 Java 版账号游玩") with mochaMaroon.bedrockMapped
}

val MEMBER_MODIFY_UNLINK_BE_SUCCEED = component {
    text("已成功为玩家 ") with mochaPink
    text("<player> ") with mochaFlamingo
    text("解绑基岩版账号") with mochaPink
}

val MEMBER_MODIFY_HIDE_SUCCEED = component {
    text("已成功隐藏玩家 ") with mochaPink
    text("<player>") with mochaFlamingo
}

val MEMBER_MODIFY_UN_HIDE_SUCCEED = component {
    text("已成功取消玩家 ") with mochaPink
    text("<player> ") with mochaFlamingo
    text("的隐藏状态") with mochaPink
}

val MEMBER_MODIFY_HIDE_FAILED = component {
    text("玩家 ") with mochaMaroon
    text("<player> ") with mochaFlamingo
    text("已处于隐藏状态") with mochaMaroon
}

val MEMBER_MODIFY_UN_HIDE_FAILED = component {
    text("玩家 ") with mochaMaroon
    text("<player> ") with mochaFlamingo
    text("未处于隐藏状态") with mochaMaroon
}

val MEMBER_LOOKUP = component {
    text("UID: ") with mochaText
    text("<uid>") with mochaPink
    newline()
    text("UUID: ") with mochaText
    text("<id>") with mochaPink
    newline()
    text("玩家名: ") with mochaText
    text("<name>") with mochaPink
    newline()
    text("格式化玩家名: ") with mochaText
    text("<rawName>") with mochaPink
    newline()
    text("白名单状态: ") with mochaText
    text("<whitelistStatus>") with mochaPink
    newline()
    text("验证类型: ") with mochaText
    text("<authType>") with mochaPink
    newline()
    text("创建时间: ") with mochaText
    text("<createdAt>") with mochaPink
    newline()
    text("上次登录时间: ") with mochaText
    text("<lastJoinedAt>") with mochaPink
    newline()
    text("上次退出时间: ") with mochaText
    text("<lastQuitedAt>") with mochaPink
    newline()
    text("数据容器: ") with mochaText
    text("<dataContainer>") with mochaPink
    newline()
    text("基岩版账号: ") with mochaText
    text("<bedrockAccount>") with mochaPink
    newline()
    text("个性签名: ") with mochaText
    text("<bio>") with mochaPink
    newline()
    text("隐藏状态: ") with mochaText
    text("<isHidden>") with mochaPink
}