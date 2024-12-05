package ink.pmc.framework.interactive

import cafe.adriel.voyager.navigator.Navigator
import ink.pmc.framework.frameworkPaper
import ink.pmc.framework.interactive.inventory.InventoryScope
import org.bukkit.entity.Player
import java.util.concurrent.ConcurrentHashMap

class GuiManagerImpl : GuiManager {
    private val inventoryScopes = ConcurrentHashMap<Player, GuiInventoryScope>()

    override fun get(player: Player): GuiScope<*>? {
        return inventoryScopes[player]
    }

    override fun getInventory(player: Player): GuiInventoryScope? {
        return inventoryScopes[player]
    }

    override fun has(player: Player): Boolean {
        return get(player) != null
    }

    override fun hasInventory(player: Player): Boolean {
        return getInventory(player) != null
    }


    private fun disposeExistedScope(player: Player) {
        if (!has(player)) return
        frameworkPaper.logger.warning("Player ${player.name} has running Inventory/Form scope, disposing it before launch another")
        dispose(player)
    }

    override fun startInventory(player: Player, contents: ComposableFunction): GuiInventoryScope {
        disposeExistedScope(player)
        return InventoryScope(player, contents).also {
            inventoryScopes[player] = it
        }
    }

    override fun startScreen(player: Player, screen: InteractiveScreen) {
        startInventory(player) {
            Navigator(screen)
        }
    }

    override fun removeScope(scope: GuiScope<*>) {
        if (!scope.isDisposed) scope.dispose()
        inventoryScopes.values.remove(scope)
    }

    override fun dispose(player: Player) {
        get(player)?.dispose()
    }

    override fun disposeAll() {
        inventoryScopes.values.forEach { it.dispose() }
        inventoryScopes.clear()
    }
}