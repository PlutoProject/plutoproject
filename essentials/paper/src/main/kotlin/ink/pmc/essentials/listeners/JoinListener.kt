package ink.pmc.essentials.listeners

import ink.pmc.essentials.config.EssentialsConfig
import ink.pmc.essentials.items.MENU_ITEM
import ink.pmc.essentials.items.SERVER_SELECTOR_ITEM
import ink.pmc.framework.playerdb.PlayerDb
import ink.pmc.framework.utils.inventory.isFull
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

@Suppress("UNUSED", "UnusedReceiverParameter")
object JoinListener : Listener, KoinComponent {
    private val config by lazy { get<EssentialsConfig>().join }

    private suspend fun Player.isItemGiven(name: String): Boolean {
        val db = PlayerDb.getOrCreate(uniqueId)
        return db.getBoolean("essentials.join.item_given.$name")
    }

    private suspend fun Player.setItemGiven(name: String) {
        val db = PlayerDb.getOrCreate(uniqueId)
        db["essentials.join.item_given.$name"] = true
        db.update()
    }

    @EventHandler
    suspend fun PlayerJoinEvent.menu() {
        if (!config.menuItem) return
        if (player.isItemGiven("menu_item") || player.inventory.isFull) return
        player.inventory.addItem(MENU_ITEM)
        player.setItemGiven("menu_item")
    }

    @EventHandler
    suspend fun PlayerJoinEvent.serverSelectorItem() {
        if (!config.serverSelectorItem) return
        if (player.isItemGiven("server_selector_item") || player.inventory.isFull) return
        player.inventory.addItem(SERVER_SELECTOR_ITEM)
        player.setItemGiven("server_selector_item")
    }
}