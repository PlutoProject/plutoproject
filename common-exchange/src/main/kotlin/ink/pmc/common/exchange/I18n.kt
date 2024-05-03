package ink.pmc.common.exchange

import ink.pmc.common.utils.visual.*
import net.kyori.adventure.text.Component

val CHECKOUT_OVER_SIZE
    get() = Component.text("<amount> 个物品由于没有足够的背包空间，被丢到了地上").color(mochaMaroon)

val MATERIAL_NOT_AVAILABLE_NAME
    get() = Component.text("<material> 不可购买").color(mochaMaroon)

val MATERIAL_NOT_AVAILABLE_LORE
    get() = listOf(Component.text("若你觉得有必要添加进兑换商店，可以向我们反馈").color(mochaSubtext0))

val TICKETS_LOOKUP_SELF
    get() = Component.text("你目前拥有 <amount> 个兑换券").color(mochaPink)

val TICKETS_LOOKUP_OTHER
    get() = Component.text("玩家 <player> 拥有 <amount> 个兑换券").color(mochaPink)

val TICKETS_SET_SUCCEED
    get() = Component.text("已将玩家 <player> 的兑换券设置为 <amount> 个").color(mochaGreen)

val TICKETS_DEPOSIT_SUCCEED
    get() = Component.text("已为玩家 <player> 增加 <amount> 个兑换券").color(mochaGreen)

val TICKETS_WITHDRAW_SUCCEED
    get() = Component.text("已为玩家 <player> 减少 <amount> 个兑换券").color(mochaGreen)

val TICKETS_WITHDRAW_FAILED_NOT_ENOUGH
    get() = Component.text("玩家 <player> 没有这么多兑换券").color(mochaMaroon)

val CHECKOUT_SUCCEED
    get() = Component.text("结账成功！你此次购买了 <amount> 个物品").color(mochaGreen)

val CHECKOUT_FAILED_TICKETS_NOT_ENOUGH
    get() = Component.text("你的兑换券不足，需要 <amount> 个兑换券").color(mochaMaroon)
        .appendNewline()
        .append(Component.text("请减少背包内的待购买物品，然后再试").color(mochaSubtext0))