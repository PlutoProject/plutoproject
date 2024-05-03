package ink.pmc.common.exchange

import ink.pmc.common.utils.visual.mochaMaroon
import ink.pmc.common.utils.visual.mochaSubtext0
import net.kyori.adventure.text.Component

val CHECKOUT_OVER_SIZE
    get() = Component.text("<amount> 个物品由于没有足够的背包空间，被丢到了地上").color(mochaMaroon)

val MATERIAL_NOT_AVAILABLE_NAME
    get() = Component.text("<material> 不可购买").color(mochaMaroon)

val MATERIAL_NOT_AVAILABLE_LORE
    get() = listOf(Component.text("若你觉得有必要添加进兑换商店，可以向我们反馈").color(mochaSubtext0))