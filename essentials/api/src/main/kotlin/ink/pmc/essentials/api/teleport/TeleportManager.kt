package ink.pmc.essentials.api.teleport

import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

@Suppress("UNUSED")
interface TeleportManager {

    fun teleport(player: Player, destination: Location, prompt: Boolean = true)

    suspend fun launchSuspend(player: Player, destination: Location, prompt: Boolean = true)

    fun teleport(player: Player, destination: Player, prompt: Boolean = true)

    suspend fun launchSuspend(player: Player, destination: Player, prompt: Boolean = true)

    fun teleport(player: Player, destination: Entity, prompt: Boolean = true)

    suspend fun launchSuspend(player: Player, destination: Entity, prompt: Boolean = true)

}