package ink.pmc.essentials.api.teleport

import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import java.util.*

@Suppress("UNUSED")
interface TeleportManager {

    val teleportRequests: Collection<TeleportRequest>
    val defaultOption: TeleportOption

    fun getRequest(id: UUID): TeleportRequest?

    fun getRequests(player: Player): Collection<TeleportRequest>

    fun hasRequest(id: UUID): Boolean

    fun hasUnfinishedRequest(player: Player): Boolean

    fun createRequest(
        source: Player,
        destination: Player,
        direction: TeleportDirection,
        option: TeleportOption = defaultOption,
        prompt: Boolean = true
    ): TeleportRequest?

    fun removeRequest(id: UUID, prompt: Boolean = true)

    fun clearRequest(prompt: Boolean = false)

    fun teleport(player: Player, destination: Location, prompt: Boolean = true)

    suspend fun teleportSuspend(player: Player, destination: Location, prompt: Boolean = true)

    fun teleport(player: Player, destination: Player, prompt: Boolean = true)

    suspend fun teleportSuspend(player: Player, destination: Player, prompt: Boolean = true)

    fun teleport(player: Player, destination: Entity, prompt: Boolean = true)

    suspend fun teleportSuspend(player: Player, destination: Entity, prompt: Boolean = true)

}