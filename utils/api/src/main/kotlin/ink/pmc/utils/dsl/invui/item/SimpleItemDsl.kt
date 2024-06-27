package ink.pmc.utils.dsl.invui.item

import ink.pmc.utils.dsl.invui.gui.GuiDsl
import xyz.xenondevs.invui.item.impl.SimpleItem

class SimpleItemDsl : ItemDsl<SimpleItem>() {

    override fun build(): SimpleItem {
        return SimpleItem(itemProvider) { clickHandler?.let { handler -> handler(it) } }
            .apply { item = this }
    }

}

fun GuiDsl<*>.simpleItem(char: Char, item: SimpleItemDsl.() -> Unit) {
    ingredient(char, SimpleItemDsl().apply(item).build())
}