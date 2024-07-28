package ink.pmc.essentials.api.teleport

import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import java.util.*

@Suppress("UNUSED")
interface TeleportManager {

    val teleportRequests: Collection<TeleportRequest>
    val queue: Queue<TeleportTask>
    val defaultRequestOptions: RequestOptions
    val defaultTeleportOptions: TeleportOptions
    val worldTeleportOptions: Map<World, TeleportOptions>
    val blacklistedWorlds: Collection<World>

    fun getWorldTeleportOptions(world: World): TeleportOptions

    fun getRequest(id: UUID): TeleportRequest?

    fun hasRequest(id: UUID): Boolean

    fun getSentRequests(player: Player): Collection<TeleportRequest>

    fun getReceivedRequests(player: Player): Collection<TeleportRequest>

    fun hasUnfinishedRequest(player: Player): Boolean

    fun getUnfinishedRequest(player: Player): TeleportRequest?

    fun hasPendingRequest(player: Player): Boolean

    fun getPendingRequest(player: Player): TeleportRequest?

    fun createRequest(
        source: Player,
        destination: Player,
        direction: TeleportDirection,
        options: RequestOptions = defaultRequestOptions,
    ): TeleportRequest?

    fun cancelRequest(id: UUID)

    fun cancelRequest(request: TeleportRequest)

    fun removeRequest(id: UUID)

    fun clearRequest()

    fun teleport(player: Player, destination: Location, teleportOptions: TeleportOptions? = null, prompt: Boolean = true)

    fun isSafe(location: Location, teleportOptions: TeleportOptions? = null): Boolean

    suspend fun searchSafeLocationSuspend(start: Location, teleportOptions: TeleportOptions? = null): Location?

    fun searchSafeLocation(start: Location, teleportOptions: TeleportOptions? = null): Location?

    suspend fun teleportSuspend(
        player: Player,
        destination: Location,
        teleportOptions: TeleportOptions? = null,
        prompt: Boolean = true
    )

    fun teleport(player: Player, destination: Player, teleportOptions: TeleportOptions? = null, prompt: Boolean = true)

    suspend fun teleportSuspend(
        player: Player,
        destination: Player,
        teleportOptions: TeleportOptions? = null,
        prompt: Boolean = true
    )

    fun teleport(player: Player, destination: Entity, teleportOptions: TeleportOptions? = null, prompt: Boolean = true)

    suspend fun teleportSuspend(
        player: Player,
        destination: Entity,
        teleportOptions: TeleportOptions? = null,
        prompt: Boolean = true
    )

    fun tick()

}