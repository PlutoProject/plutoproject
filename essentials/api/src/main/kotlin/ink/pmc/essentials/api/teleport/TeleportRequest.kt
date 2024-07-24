package ink.pmc.essentials.api.teleport

import org.bukkit.entity.Player
import java.time.Instant
import java.util.UUID

@Suppress("UNUSED")
interface TeleportRequest {

    val id: UUID
    val option: TeleportOption
    val source: Player
    val destination: Player
    val direction: TeleportDirection
    val createdAt: Instant
    val status: TeleportStatus

    fun cancel(prompt: Boolean = true)

}