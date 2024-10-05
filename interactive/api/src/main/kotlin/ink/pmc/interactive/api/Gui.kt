package ink.pmc.interactive.api

import ink.pmc.interactive.api.inventory.layout.InventoryNode
import ink.pmc.utils.inject.inlinedGet
import org.bukkit.entity.Player

typealias GuiInventoryScope = GuiScope<InventoryNode>

interface Gui {

    companion object : Gui by inlinedGet()

    fun get(player: Player): GuiScope<*>?

    fun getInventory(player: Player): GuiInventoryScope?

    fun has(player: Player): Boolean

    fun hasInventory(player: Player): Boolean

    fun startInventory(player: Player, contents: ComposableFunction): GuiInventoryScope

    fun removeScope(scope: GuiScope<*>)

    fun dispose(player: Player)

    fun disposeAll()

}