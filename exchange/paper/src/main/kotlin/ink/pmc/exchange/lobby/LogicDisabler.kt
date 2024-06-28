package ink.pmc.exchange.lobby

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntitySpawnEvent

@Suppress("UNUSED")
object LogicDisabler : Listener {

    @EventHandler
    fun entitySpawnEvent(event: EntitySpawnEvent) {
        event.isCancelled = true
    }
}