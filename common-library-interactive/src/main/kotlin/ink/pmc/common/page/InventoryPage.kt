package ink.pmc.common.page

import ink.pmc.common.component.ItemComponent

@Suppress("UNUSED")
interface InventoryPage : Page {

    fun addComponent(slot: Int, component: ItemComponent)

    fun removeComponent(slot: Int)

    fun getComponent(slot: Int): ItemComponent?

    fun isEmpty(slot: Int): Boolean

    fun mask(mask: Array<String>)

    fun maskComponent(char: Char, component: ItemComponent)

}