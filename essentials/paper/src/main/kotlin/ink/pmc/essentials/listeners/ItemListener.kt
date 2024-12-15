package ink.pmc.essentials.listeners

import ink.pmc.essentials.config.EssentialsConfig
import ink.pmc.essentials.items.isNotebookItem
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

@Suppress("UNUSED", "UnusedReceiverParameter")
object ItemListener : Listener, KoinComponent {
    private val config by lazy { get<EssentialsConfig>().item }

    @EventHandler
    fun PlayerInteractEvent.menu() {
        if (!config.menu) return
        if (!action.isRightClick || item?.isNotebookItem == false) return
        item?.let {
            isCancelled = true
            hand?.let { player.swingHand(it) }
            player.performCommand("plutoproject_menu:menu")
        }
    }
}