package ink.pmc.transfer.lobby

import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.util.Vector
import java.util.*

typealias Handler = (player: Player) -> Unit
typealias TypedHandler = Pair<PortalBounding.HandlerType, Handler>

@Suppress("UNUSED")
class PortalBounding(private val a: Location, private val b: Location) {

    private val handlers = mutableListOf<TypedHandler>()
    val listener = BoundingListener(this)

    enum class HandlerType {
        ENTER, EXIT
    }

    fun addHandler(type: HandlerType, block: Handler) {
        handlers.add(type to block)
    }

    class BoundingListener(private val bounding: PortalBounding) : Listener {

        private val inBounding = mutableSetOf<UUID>()
        private val vecA = bounding.a.toVector()
        private val vecB = bounding.b.toVector()
        private val world = bounding.a.world

        @EventHandler
        fun playerMoveEvent(event: PlayerMoveEvent) {
            val player = event.player

            if (player.world != world) {
                return
            }

            val uuid = player.uniqueId
            val min = Vector.getMinimum(vecA, vecB)
            val max = Vector.getMaximum(vecA, vecB)
            val playerVec = player.location.toVector()
            val isInAABB = playerVec.isInAABB(min, max)

            if (isInAABB && inBounding.contains(uuid)) {
                return
            }

            if (isInAABB && !inBounding.contains(uuid)) {
                inBounding.add(uuid)
                bounding.handlers.filter { it.first == HandlerType.ENTER }.forEach { it.second(player) }
                return
            }

            if (!isInAABB && !inBounding.contains(uuid)) {
                return
            }

            if (!isInAABB && inBounding.contains(uuid)) {
                inBounding.remove(uuid)
                bounding.handlers.filter { it.first == HandlerType.EXIT }.forEach { it.second(player) }
                return
            }
        }

    }

}