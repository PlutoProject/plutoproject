package ink.pmc.essentials.listeners

import ink.pmc.essentials.config.EssentialsConfig
import ink.pmc.essentials.items.isMenuItem
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

@Suppress("UNUSED", "UnusedReceiverParameter")
object ItemListener : Listener, KoinComponent {
    private val config by lazy { get<EssentialsConfig>().item }

    @EventHandler
    fun PlayerInteractEvent.menu() {
        if (!config.menu) return
        if (hand != EquipmentSlot.HAND) {
            if (player.inventory.itemInMainHand.isMenuItem) {
                isCancelled = true
            }
            return
        }
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return
        }
        item?.let {
            if (!it.isMenuItem) return
            isCancelled = true
            player.performCommand("menu:menu")
        }
    }
}