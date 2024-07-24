package ink.pmc.essentials.api.teleport

import org.bukkit.entity.Player
import java.time.Instant
import java.util.UUID

@Suppress("UNUSED")
interface TeleportRequest {

    val id: UUID
    val option: RequestOptions
    val source: Player
    val destination: Player
    val direction: TeleportDirection
    val createdAt: Instant
    val status: RequestStatus

    fun cancel(prompt: Boolean = true)

}