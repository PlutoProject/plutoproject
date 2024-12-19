package ink.pmc.framework.interactive

import ink.pmc.framework.interactive.layout.InventoryNode
import ink.pmc.framework.inject.inlinedGet
import org.bukkit.entity.Player

typealias GuiInventoryScope = GuiScope<InventoryNode>

interface GuiManager {

    companion object : GuiManager by inlinedGet()

    fun get(player: Player): GuiScope<*>?

    fun getInventory(player: Player): GuiInventoryScope?

    fun has(player: Player): Boolean

    fun hasInventory(player: Player): Boolean

    fun startInventory(player: Player, contents: ComposableFunction): GuiInventoryScope

    fun startScreen(player: Player, screen: InteractiveScreen)

    fun removeScope(scope: GuiScope<*>)

    fun dispose(player: Player)

    fun disposeAll()

}