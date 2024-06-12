package ink.pmc.exchange

import ink.pmc.advkt.component.component
import ink.pmc.advkt.component.newline
import ink.pmc.advkt.component.text
import ink.pmc.utils.visual.*

val MATERIAL_NOT_AVAILABLE_NAME = component {
    text("<material> 不可购买") with mochaMaroon
}

val MATERIAL_NOT_AVAILABLE_LORE = listOf(
    component {
        text("若你觉得有必要添加进兑换，可以向我们反馈") with mochaSubtext0
    }
)

val TICKETS_LOOKUP_SELF = component {
    text("你目前拥有 <amount> 个兑换券") with mochaPink
}

val TICKETS_LOOKUP_OTHER = component {
    text("玩家 <player> 拥有 <amount> 个兑换券") with mochaPink
}

val TICKETS_SET_SUCCEED = component {
    text("已将玩家 <player> 的兑换券设置为 <amount> 个") with mochaGreen
}

val TICKETS_DEPOSIT_SUCCEED = component {
    text("已为玩家 <player> 增加 <amount> 个兑换券") with mochaGreen
}

val TICKETS_WITHDRAW_SUCCEED = component {
    text("已为玩家 <player> 减少 <amount> 个兑换券") with mochaGreen
}

val TICKETS_WITHDRAW_FAILED_NOT_ENOUGH = component {
    text("玩家 <player> 没有这么多兑换券") with mochaMaroon
}

val TICKETS_DISTRIBUTE_SUCCEED = component {
    text("今天兑换券数量已重置，你现在拥有 <amount> 个兑换券") with mochaText
}

val EXCHANGE_LOBBY_OFFLINE = component {
    text("兑换大厅正处于离线状态") with mochaMaroon
    newline()
    text("这可能是服务器内部发生了一些问题") with mochaSubtext0
    newline()
    text("请将问题报告给管理员，以便我们进行改进") with mochaSubtext0
}

val EXCHANGE_START_SUCCEED = component {
    text("正在将你传送到兑换大厅...") with mochaGreen
}

val EXCHANGE_START_FAILED_ALREADY_IN = component {
    text("你已经正在兑换大厅中了！") with mochaMaroon
}

val EXCHANGE_START_FAILED_UNKNOWN = component {
    text("在尝试进入兑换大厅时出现了未知错误") with mochaMaroon
    newline()
    text("请将问题报告给管理员，以便我们进行改进") with mochaSubtext0
}

val EXCHANGE_END_SUCCEED = component {
    text("正在将你传送到原本的位置...") with mochaGreen
}

val EXCHANGE_END_FAILED_NOT_IN = component {
    text("你并不在兑换大厅中！") with mochaMaroon
}

val EXCHANGE_END_FAILED_UNKNOWN = component {
    text("在尝试回到原本的位置时出现了未知错误") with mochaMaroon
    newline()
    text("请放心，这不会导致你的数据丢失") with mochaSubtext0
    newline()
    text("将问题报告给管理员，以便我们进行改进。随后，重新进入服务器") with mochaSubtext0
}

val CHECKOUT_SUCCEED = component {
    text("结账成功！此次购买花费了 <amount> 个兑换券") with mochaGreen
}

val CHECKOUT_SUCCEED_EMPTY = component {
    text("你看起来什么都没买呢") with mochaSubtext0
}

val CHECKOUT_OVER_SIZE = component {
    text("<amount> 个物品由于没有足够的背包空间，被丢到了地上") with mochaSubtext0
}

val CHECKOUT_FAILED_TICKETS_NOT_ENOUGH = component {
    text("你的兑换券不足，需要 <amount> 个兑换券") with mochaMaroon
    newline()
    text("请减少背包内的待购买物品，然后再试") with mochaSubtext0
}

val CHECKOUT_FAILED_UNKNOWN_ISSUE = component {
    text("出现未知错误，请联系管理员") with mochaMaroon
}

val EXCHANGE_ADMIN_START_FAILED_ALREADY_IN = component {
    text("玩家 <player> 已经在兑换大厅中了") with mochaMaroon
}

val EXCHANGE_ADMIN_START_SUCCEED = component {
    text("正在将玩家 <player> 传送到兑换大厅...") with mochaGreen
}

val EXCHANGE_ADMIN_END_FAILED_NOT_IN = component {
    text("玩家 <player> 不在兑换大厅中") with mochaMaroon
}

val EXCHANGE_ADMIN_END_SUCCEED = component {
    text("正在将玩家 <player> 传送出兑换大厅...") with mochaGreen
}

val EXCHANGE_ADMIN_CHECKOUT_SUCCEED = component {
    text("正在为玩家 <player> 结账...") with mochaGreen
}

val EXCHANGE_ADMIN_MARK_AS_CHECKOUT_SIGN_SUCCEED = component {
    text("已成功将指向的方块标记为结账告示牌") with mochaGreen
}

val EXCHANGE_ADMIN_MARK_AS_CHECKOUT_SIGN_FAILED_ALREADY_IS = component {
    text("该告示牌已经被标记为结账告示牌") with mochaMaroon
}

val EXCHANGE_ADMIN_MARK_AS_CHECKOUT_SIGN_FAILED_NOT_SIGN = component {
    text("指向的方块不是告示牌") with mochaMaroon
}

val EXCHANGE_ADMIN_MARK_AS_CHECKOUT_SIGN_NOT_FOUNT_IN_RANGE = component {
    text("交互距离内未找到方块") with mochaMaroon
}

val EXCHANGE_ADMIN_MARK_AS_CHECKOUT_SIGN_FAILED_NOT_IN_EXCHANGE = component {
    text("你不在兑换大厅中") with mochaMaroon
}

val TICKETS_RANDOM_GOT = component {
    text("你幸运的获取了一个兑换券！") with mochaFlamingo
}