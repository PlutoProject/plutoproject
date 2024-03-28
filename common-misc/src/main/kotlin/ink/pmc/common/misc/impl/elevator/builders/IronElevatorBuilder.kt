package ink.pmc.common.misc.impl.elevator.builders

import ink.pmc.common.misc.api.elevator.ElevatorBuilder
import ink.pmc.common.utils.concurrent.submitSync
import ink.pmc.common.utils.world.rawLocation
import org.bukkit.Location
import org.bukkit.Material

@Suppress("UNUSED")
class IronElevatorBuilder(override val startPoint: Location) : ElevatorBuilder {

    override val type: Material = Material.IRON_BLOCK
    override val permission: String? = null

    override suspend fun findLocations(): List<Location> {
        val loc = startPoint.rawLocation
        val offsetUp = mutableListOf<Location>()
        val offsetDown = mutableListOf<Location>()
        val result = mutableListOf<Location>()

        val up = loc.submitSync {
            val top = loc.world.maxHeight
            val curr = loc.blockY
            val temp = mutableListOf<Location>()

            for (i in loc.blockY..top) {
                val offset = i - curr
                val block = loc.clone().add(0.0, offset.toDouble(), 0.0)

                if (block.block.type != type) {
                    continue
                }

                temp.add(block)
            }

            offsetUp.addAll(filterSafe(temp))
        }

        val down = loc.submitSync {
            val bottom = loc.world.minHeight
            val curr = loc.blockY
            val temp = mutableListOf<Location>()

            for (i in bottom..loc.blockY) {
                val offset = curr - i
                val block = loc.clone().subtract(0.0, offset.toDouble(), 0.0)

                if (block.block.type != type) {
                    continue
                }

                temp.add(block)
            }

            offsetDown.addAll(filterSafe(temp))
        }

        up.join()
        down.join()

        result.addAll(offsetDown)
        result.addAll(offsetUp)

        return result
    }

    override suspend fun teleportLocations(): List<Location> {
        return findLocations().map {
            it.clone().add(0.0, 1.0, 0.0)
        }
    }

    private fun filterSafe(list: List<Location>): List<Location> {
        val filtered = list.filter {
            val offset1 = it.clone().add(0.0, 1.0, 0.0)
            val offset2 = it.clone().add(0.0, 2.0, 0.0)

            offset1.block.type == Material.AIR && offset2.block.type == Material.AIR
        }

        return filtered
    }

}