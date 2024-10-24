package ink.pmc.framework.utils.dsl.invui.gui

import ink.pmc.framework.utils.dsl.invui.window.WindowDsl
import xyz.xenondevs.invui.gui.Gui
import java.util.function.Consumer

class NormalGuiDsl : GuiDsl<Gui>() {

    override fun build(): Gui {
        return Gui.normal()
            .setStructure(*structure.toTypedArray())
            .setBackground(background)
            .setModifiers(modifiers.map { modifier -> Consumer { modifier(it) } })
            .setFrozen(frozen)
            .setIgnoreObscuredInventorySlots(ignoreObscuredInventorySlots)
            .apply {
                itemIngredients.forEach { (char, item) -> addIngredient(char, item) }
                providerIngredients.forEach { (char, item) -> addIngredient(char, item) }
                markerIngredients.forEach { (char, item) -> addIngredient(char, item) }
                slotIngredients.forEach { (char, item) -> addIngredient(char, item) }
                inventoryIngredients.forEach { (char, item) -> addIngredient(char, item) }
            }
            .build()
            .apply { gui = this }
    }

}

inline fun normalGui(block: NormalGuiDsl.() -> Unit): Gui {
    return NormalGuiDsl().apply(block).build()
}

inline fun WindowDsl<*>.gui(block: NormalGuiDsl.() -> Unit) {
    gui(normalGui(block))
}