package ink.pmc.hypervisor.listeners

import ink.pmc.hypervisor.handlePlayerPlaceBlock
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