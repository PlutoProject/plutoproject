package ink.pmc.utils.dsl.invui.window

import ink.pmc.advkt.component.RootComponentKt
import ink.pmc.utils.structure.Builder
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper
import xyz.xenondevs.invui.gui.Gui
import xyz.xenondevs.invui.window.Window

typealias WindowHandler = (player: Player) -> Unit
typealias WindowClickHandler = (player: Player, event: InventoryClickEvent) -> Unit

abstract class WindowDsl<T : Window> : Builder<Window> {

    lateinit var window: Window
    var title: Component = Component.empty()
    var viewer: Player? = null
    var gui: Gui? = null
    private var dirtyGui = false
    protected var openHandlers = mutableSetOf<WindowHandler>()
    protected var closeHandlers = mutableSetOf<WindowHandler>()
    protected var outsideClickHandlers = mutableSetOf<WindowClickHandler>()

    fun title(component: RootComponentKt.() -> Unit) {
        title = RootComponentKt().apply(component).build()
    }

    fun changeTitle(component: Component) {
        window.changeTitle(AdventureComponentWrapper(component))
    }

    fun changeTitle(component: RootComponentKt.() -> Unit) {
        changeTitle(RootComponentKt().apply(component).build())
    }

    fun gui(gui: Gui) {
        if (this.gui == null || dirtyGui) {
            this.gui = gui
            return
        }
        updateGui(gui)
    }

    private fun updateGui(gui: Gui) {
        dirtyGui = true
        gui(gui)
        build().open()
        dirtyGui = false
    }

    fun onOpen(block: (Player) -> Unit) {
        openHandlers.add {
            if (dirtyGui) {
                return@add
            }

            block(it)
        }
    }

    fun onClose(block: (Player) -> Unit) {
        closeHandlers.add {
            if (dirtyGui) {
                return@add
            }

            block(it)
        }
    }

    fun onOutsideClick(block: (Player, InventoryClickEvent) -> Unit) {
        outsideClickHandlers.add { player, event ->
            if (dirtyGui) {
                return@add
            }

            block(player, event)
        }
    }

    fun openWindow() {
        window.open()
    }

    fun closeWindow() {
        window.close()
    }

    abstract override fun build(): T

}