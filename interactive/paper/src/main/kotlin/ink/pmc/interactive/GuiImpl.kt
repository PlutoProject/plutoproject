package ink.pmc.interactive

import ink.pmc.interactive.api.*
import ink.pmc.interactive.form.FormScope
import ink.pmc.interactive.inventory.InventoryScope
import org.bukkit.entity.Player
import java.util.concurrent.ConcurrentHashMap

class GuiImpl : Gui {

    private val inventoryScopes = ConcurrentHashMap<Player, GuiInventoryScope>()
    private val formScopes = ConcurrentHashMap<Player, GuiFormScope>()

    override fun get(player: Player): GuiScope<*>? {
        return inventoryScopes[player] ?: return formScopes[player]
    }

    override fun getInventory(player: Player): GuiInventoryScope? {
        return inventoryScopes[player]
    }

    override fun getForm(player: Player): GuiFormScope? {
        return formScopes[player]
    }

    override fun has(player: Player): Boolean {
        return get(player) != null
    }

    override fun hasInventory(player: Player): Boolean {
        return getInventory(player) != null
    }

    override fun hasForm(player: Player): Boolean {
        return getForm(player) != null
    }

    private fun disposeExistedScope(player: Player) {
        if (!has(player)) return
        plugin.logger.warning("Player ${player.name} has running Inventory/Form scope, disposing it before launch another")
        dispose(player)
    }

    override fun startInventory(player: Player, contents: ComposableFunction): GuiInventoryScope {
        disposeExistedScope(player)
        return InventoryScope(player, contents).also {
            inventoryScopes[player] = it
        }
    }

    override fun startForm(player: Player, contents: ComposableFunction): GuiFormScope {
        disposeExistedScope(player)
        return FormScope(player, contents).also {
            formScopes[player] = it
        }
    }

    override fun removeScope(scope: GuiScope<*>) {
        if (!scope.isDisposed) scope.dispose()
        inventoryScopes.values.remove(scope)
        formScopes.values.remove(scope)
    }

    override fun dispose(player: Player) {
        get(player)?.dispose()
    }

    override fun disposeAll() {
        // 拷贝一次再操作，防止 CME
        inventoryScopes.values.toMutableList().forEach { it.dispose() }
        formScopes.values.toMutableList().forEach { it.dispose() }
        inventoryScopes.clear()
        formScopes.clear()
    }

}