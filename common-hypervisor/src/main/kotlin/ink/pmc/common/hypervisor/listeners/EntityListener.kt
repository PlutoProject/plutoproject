package ink.pmc.common.hypervisor.listeners

import ink.pmc.common.hypervisor.handleEndermanPlaceBlock
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