package ink.pmc.essentials.listeners

import ink.pmc.essentials.config.EssentialsConfig
import ink.pmc.essentials.items.isNotebookItem
import ink.pmc.essentials.items.isServerSelectorItem
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

    private fun PlayerInteractEvent.checkCondition(): Boolean {
        if (hand != EquipmentSlot.HAND) {
            if (player.inventory.itemInMainHand.isNotebookItem) {
                isCancelled = true
            }
            return false
        }
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return false
        }
        return true
    }

    @EventHandler
    fun PlayerInteractEvent.menu() {
        if (!config.menu) return
        if (!checkCondition()) return
        item?.let {
            if (!it.isNotebookItem) return
            isCancelled = true
            player.performCommand("plutoproject_menu:menu")
        }
    }

    @EventHandler
    fun PlayerInteractEvent.serverSelector() {
        if (!config.serverSelector) return
        if (!checkCondition()) return
        item?.let {
            if (!it.isServerSelectorItem) return
            isCancelled = true
            player.performCommand("plutoproject_menu:serverselector")
        }
    }
}