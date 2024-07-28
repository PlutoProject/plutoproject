package ink.pmc.essentials.api.teleport

import ink.pmc.utils.world.ValueChunkLoc
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.UUID

data class TeleportTask(
    val id: UUID,
    val player: Player,
    val destination: Location,
    val teleportOptions: TeleportOptions?,
    val prompt: Boolean,
    val chunkNeedToPrepare: List<ValueChunkLoc>,
)