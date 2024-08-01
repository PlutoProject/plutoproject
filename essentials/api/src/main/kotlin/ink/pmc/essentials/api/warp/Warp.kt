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

    fun teleport(player: Player, cost: Boolean = true, prompt: Boolean = true)

    suspend fun teleportSuspend(player: Player, cost: Boolean = true, prompt: Boolean = true)

    suspend fun save()

}