package ink.pmc.common.hypervisor.listeners

import ink.pmc.common.hypervisor.handlePlayerPlaceBlock
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent

@Suppress("UNUSED")
object PlayerListener : Listener {

    @EventHandler
    fun blockPlaceEvent(event: BlockPlaceEvent) {
        handlePlayerPlaceBlock(event)
    }

}