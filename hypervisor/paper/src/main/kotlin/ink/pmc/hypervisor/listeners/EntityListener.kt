package ink.pmc.hypervisor.listeners

import ink.pmc.hypervisor.handleEndermanPlaceBlock
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityChangeBlockEvent

@Suppress("UNUSED")
object EntityListener : Listener {

    @EventHandler
    fun entityChangeBlockEvent(event: EntityChangeBlockEvent) {
        handleEndermanPlaceBlock(event)
    }

}