package ink.pmc.interactive.api.gui

import ink.pmc.interactive.api.ComposableFunction
import ink.pmc.interactive.api.gui.form.GeneralFormNode
import ink.pmc.interactive.api.gui.inventory.layout.InventoryNode
import ink.pmc.utils.inject.inlinedGet
import org.bukkit.entity.Player

typealias GuiFormScope = GuiScope<GeneralFormNode>
typealias GuiInventoryScope = GuiScope<InventoryNode>

interface Gui {

    companion object : Gui by inlinedGet()

    fun get(player: Player): GuiScope<*>?

    fun getInventory(player: Player): GuiInventoryScope?

    fun getForm(player: Player): GuiFormScope?

    fun has(player: Player): Boolean

    fun hasInventory(player: Player): Boolean

    fun hasForm(player: Player): Boolean

    fun startInventory(player: Player, contents: ComposableFunction): GuiInventoryScope

    fun startForm(player: Player, contents: ComposableFunction): GuiFormScope

    fun removeScope(scope: GuiScope<*>)

    fun dispose(player: Player)

    fun disposeAll()

}