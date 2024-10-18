package ink.pmc.essentials.listeners

import ink.pmc.essentials.JOIN_MENU_ITEM_GIVEN_KEY
import ink.pmc.essentials.config.EssentialsConfig
import ink.pmc.essentials.item.MENU_ITEM
import ink.pmc.playerdb.api.PlayerDb
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

@Suppress("UNUSED", "UnusedReceiverParameter")
object JoinListener : Listener, KoinComponent {

    private val config by lazy { get<EssentialsConfig>().Join() }

    @EventHandler
    suspend fun PlayerJoinEvent.menuItem() {
        if (!config.menuItem) return
        val db = PlayerDb.getOrCreate(player.uniqueId)
        val given = db.getBoolean(JOIN_MENU_ITEM_GIVEN_KEY)
        if (given || !player.inventory.storageContents.contains(null)) return
        player.inventory.addItem(MENU_ITEM)
        db[JOIN_MENU_ITEM_GIVEN_KEY] = true
        db.update()
    }

}