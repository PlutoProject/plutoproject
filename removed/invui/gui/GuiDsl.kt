package ink.pmc.framework.utils.dsl.invui.gui

import ink.pmc.framework.utils.dsl.ItemStackDsl
import ink.pmc.framework.utils.structure.Builder
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import xyz.xenondevs.invui.gui.Gui
import xyz.xenondevs.invui.gui.SlotElement
import xyz.xenondevs.invui.gui.structure.Marker
import xyz.xenondevs.invui.inventory.Inventory
import xyz.xenondevs.invui.item.Item
import xyz.xenondevs.invui.item.ItemProvider
import xyz.xenondevs.invui.item.builder.ItemBuilder

typealias GuiModifier = (gui: Gui) -> Unit

abstract class GuiDsl<T : Gui> : Builder<T> {

    lateinit var gui: Gui
    protected val structure = mutableListOf<String>()
    protected val modifiers = mutableListOf<GuiModifier>()
    protected var background: ItemProvider = ItemProvider.EMPTY
    protected var itemIngredients = mutableMapOf<Char, Item>()
    protected var providerIngredients = mutableMapOf<Char, ItemProvider>()
    protected var markerIngredients = mutableMapOf<Char, Marker>()
    protected var slotIngredients = mutableMapOf<Char, SlotElement>()
    protected var inventoryIngredients = mutableMapOf<Char, Inventory>()
    var frozen: Boolean = false
    var ignoreObscuredInventorySlots = false

    fun structure(vararg data: String) {
        structure.clear()
        structure.addAll(data)
    }

    fun modifier(modifier: GuiModifier) {
        modifiers.add(modifier)
    }

    fun background(provider: ItemProvider) {
        background = provider
    }

    fun background(itemStack: ItemStack) {
        background = ItemBuilder(itemStack)
    }

    fun ingredient(char: Char, item: Item) {
        itemIngredients[char] = item
    }

    fun ingredient(char: Char, itemProvider: ItemProvider) {
        providerIngredients[char] = itemProvider
    }

    fun ingredient(char: Char, itemStack: ItemStack) {
        providerIngredients[char] = ItemBuilder(itemStack)
    }

    fun ingredient(char: Char, material: Material, amount: Int = 1, itemStack: ItemStackDsl.() -> Unit) {
        ingredient(char, ink.pmc.framework.utils.dsl.itemStack(material, amount, itemStack))
    }

    fun ingredient(char: Char, marker: Marker) {
        markerIngredients[char] = marker
    }

    fun ingredient(char: Char, slot: SlotElement) {
        slotIngredients[char] = slot
    }

    fun ingredient(char: Char, inventory: Inventory) {
        inventoryIngredients[char] = inventory
    }

    abstract override fun build(): T

}