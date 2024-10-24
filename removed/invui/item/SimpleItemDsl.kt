package ink.pmc.framework.utils.dsl.invui.item

import ink.pmc.framework.utils.dsl.invui.gui.GuiDsl
import xyz.xenondevs.invui.item.Item
import xyz.xenondevs.invui.item.impl.SimpleItem

class SimpleItemDsl : ItemDsl<SimpleItem>() {

    override fun build(): SimpleItem {
        return SimpleItem(itemProvider, clickHandler)
            .apply { item = this }
    }

}

inline fun simpleItem(item: SimpleItemDsl.() -> Unit): Item {
    return SimpleItemDsl().apply(item).build()
}

inline fun GuiDsl<*>.simpleItem(char: Char, item: SimpleItemDsl.() -> Unit) {
    ingredient(char, SimpleItemDsl().apply(item).build())
}