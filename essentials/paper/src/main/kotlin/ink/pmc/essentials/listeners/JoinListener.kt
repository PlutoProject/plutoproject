package ink.pmc.essentials.listeners

import ink.pmc.essentials.config.EssentialsConfig
import ink.pmc.essentials.items.NOTEBOOK_ITEM
import ink.pmc.essentials.items.SERVER_SELECTOR_ITEM
import ink.pmc.framework.playerdb.PlayerDb
import ink.pmc.framework.utils.inventory.isFull
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Suppress("UNUSED", "UnusedReceiverParameter")
object JoinListener : Listener, KoinComponent {
    private val config by inject<EssentialsConfig>()
    private val joinConfig by lazy { config.join }

    private suspend fun Player.isItemGiven(name: String): Boolean {
        val db = PlayerDb.getOrCreate(uniqueId)
        return db.getBoolean("essentials.${config.serverName}.join.item.$name")
    }

    private suspend fun Player.setItemGiven(name: String) {
        val db = PlayerDb.getOrCreate(uniqueId)
        db["essentials.${config.serverName}.join.item.$name"] = true
        db.update()
    }

    @EventHandler
    suspend fun PlayerJoinEvent.menu() {
        if (!joinConfig.menuItem) return
        if (player.isItemGiven("notebook") || player.inventory.isFull) return
        player.inventory.addItem(NOTEBOOK_ITEM)
        player.setItemGiven("notebook")
    }

    @EventHandler
    suspend fun PlayerJoinEvent.serverSelectorItem() {
        if (!joinConfig.serverSelectorItem) return
        if (player.isItemGiven("server_selector") || player.inventory.isFull) return
        player.inventory.addItem(SERVER_SELECTOR_ITEM)
        player.setItemGiven("server_selector")
    }
}