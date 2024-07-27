package ink.pmc.essentials.api.teleport

import org.bukkit.entity.Player
import java.time.Instant
import java.util.UUID

@Suppress("UNUSED")
interface TeleportRequest {

    val id: UUID
    val options: RequestOptions
    val source: Player
    val destination: Player
    val direction: TeleportDirection
    val createdAt: Instant
    val status: RequestStatus
    val isFinished: Boolean

    fun accept(prompt: Boolean = true)

    fun deny(prompt: Boolean = true)

    fun expire(prompt: Boolean = true)

    fun cancel(prompt: Boolean = true)

}