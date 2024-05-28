package ink.pmc.common.hypervisor

import ink.pmc.common.utils.platform.paper
import io.papermc.paper.event.entity.EntityMoveEvent
import org.bukkit.entity.EntityType
import org.bukkit.event.Listener

@Suppress("UNUSED")
object DuplicatedUuidDetector : Listener {

    fun entityMoveEvent(event: EntityMoveEvent) {
        val entity = event.entity

        if (entity.type == EntityType.FALLING_BLOCK) {
            return
        }

        val uuid = event.entity.uniqueId

        if (!paper.worlds.any { it.entities.any { e -> e.uniqueId == uuid } }) {
            return
        }

        event.isCancelled = true
        entity.remove()

        serverLogger.warning("Removed duplicated uuid entity")
    }

}