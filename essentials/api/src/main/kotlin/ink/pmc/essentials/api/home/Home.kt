package ink.pmc.essentials.api.home

import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.time.Instant
import java.util.*

@Suppress("UNUSED")
interface Home {

    val id: UUID
    var name: String
    val createdAt: Instant
    var location: Location
    val owner: OfflinePlayer

    fun teleport(player: Player, prompt: Boolean = true)

    suspend fun teleportSuspend(player: Player, prompt: Boolean = true)

    suspend fun update()

}