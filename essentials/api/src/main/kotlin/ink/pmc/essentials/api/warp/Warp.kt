package ink.pmc.essentials.api.warp

import org.bukkit.Location
import org.bukkit.entity.Player
import java.time.Instant
import java.util.*

@Suppress("UNUSED")
interface Warp {

    val id: UUID
    val name: String
    var alias: String?
    val createdAt: Instant
    var location: Location
    var isSpawn: Boolean
    var isDefaultSpawn: Boolean
    val isLoaded: Boolean

    fun teleport(player: Player, prompt: Boolean = true)

    suspend fun teleportSuspend(player: Player, prompt: Boolean = true)

    suspend fun update()

}