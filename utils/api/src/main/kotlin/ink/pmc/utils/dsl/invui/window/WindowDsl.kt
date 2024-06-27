package ink.pmc.utils.dsl.invui.window

import ink.pmc.utils.structure.Builder
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import xyz.xenondevs.invui.gui.Gui
import xyz.xenondevs.invui.window.Window

typealias WindowHandler = (player: Player) -> Unit
typealias WindowClickHandler = (Player, InventoryClickEvent) -> Unit

abstract class WindowDsl<T : Window> : Builder<Window> {

    lateinit var window: Window
    var title: Component = Component.empty()
    var viewer: Player? = null
    var gui: Gui? = null
    protected var openHandlers = mutableSetOf<WindowHandler>()
    protected var closeHandlers = mutableSetOf<WindowHandler>()
    protected var outsideClickHandlers = mutableSetOf<WindowClickHandler>()

    fun gui(gui: Gui) {
        this.gui = gui
    }

    fun onOpen(block: (Player) -> Unit) {
        openHandlers.add(block)
    }

    fun onClose(block: (Player) -> Unit) {
        closeHandlers.add(block)
    }

    fun onOutsideClick(block: (Player, InventoryClickEvent) -> Unit) {
        outsideClickHandlers.add(block)
    }

    fun openWindow() {
        window.open()
    }

    fun closeWindow() {
        window.close()
    }

    abstract override fun build(): T

}