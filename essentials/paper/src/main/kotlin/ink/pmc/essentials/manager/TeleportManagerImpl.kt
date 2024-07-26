package ink.pmc.essentials.manager

import ink.pmc.essentials.EssentialsConfig
import ink.pmc.essentials.api.teleport.*
import ink.pmc.utils.data.mapKv
import ink.pmc.utils.multiplaform.item.KeyedMaterial
import ink.pmc.utils.multiplaform.item.exts.bukkit
import ink.pmc.utils.world.ValueChunkLoc
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.time.Duration

class TeleportManagerImpl : TeleportManager, KoinComponent {

    private val conf = get<EssentialsConfig>().Teleport()

    override val teleportRequests: Collection<TeleportRequest> = mutableListOf()
    override val queue: Queue<Pair<Player, Location>> = ConcurrentLinkedQueue()
    override val defaultRequestOptions: RequestOptions = RequestOptions(
        Duration.parse(conf.requestExpireAfter),
        Duration.parse(conf.requestRemoveAfter)
    )
    override val defaultTeleportOptions: TeleportOptions = TeleportOptions(
        conf.avoidVoid,
        conf.safeLocationSearchRadius,
        conf.chunkPrepareRadius,
        conf.blacklistedBlocks.toSet(),
    )
    override val worldTeleportOptions: Map<World, TeleportOptions> = conf.worldOptions.mapKv {
        it.key to TeleportOptions(
            it.value.get("avoid-void") ?: defaultTeleportOptions.avoidVoid,
            it.value.get("safe-location-search-radius") ?: defaultTeleportOptions.safeLocationSearchRadius,
            it.value.get("chunk-prepare-radius") ?: defaultTeleportOptions.chunkPrepareRadius,
            it.value.get<List<String>>("blacklisted-blocks")?.map { m -> KeyedMaterial(m).bukkit }?.toSet()
                ?: defaultTeleportOptions.blacklistedBlocks
        )
    }
    override val blacklistedWorlds: Collection<World> = conf.blacklistedWorlds

    private val World.teleportOptions: TeleportOptions
        get() = worldTeleportOptions[this] ?: defaultTeleportOptions

    private val Location.chunkNeedPrepare: List<ValueChunkLoc>
        get() {
            val radius = world.teleportOptions.chunkPrepareRadius
            val centerChunk = ValueChunkLoc(chunk.x, chunk.z)
            return (centerChunk.x - radius..centerChunk.x + radius).flatMap { x ->
                (centerChunk.y - radius..centerChunk.y + radius).map { y ->
                    ValueChunkLoc(x, y)
                }
            }
        }

    override fun getRequest(id: UUID): TeleportRequest? {
        return teleportRequests.firstOrNull { it.id == id }
    }

    override fun getRequests(player: Player): Collection<TeleportRequest> {
        return teleportRequests.filter { it.source == player }
    }

    override fun hasRequest(id: UUID): Boolean {
        return teleportRequests.any { it.id == id }
    }

    override fun hasUnfinishedRequest(player: Player): Boolean {
        return getRequests(player).any { it.status == RequestStatus.WAITING }
    }

    override fun createRequest(
        source: Player,
        destination: Player,
        direction: TeleportDirection,
        option: RequestOptions,
        prompt: Boolean
    ): TeleportRequest? {
        TODO("Not yet implemented")
    }

    override fun removeRequest(id: UUID, prompt: Boolean) {
        TODO("Not yet implemented")
    }

    override fun clearRequest(prompt: Boolean) {
        TODO("Not yet implemented")
    }

    override fun teleport(player: Player, destination: Location, teleportOptions: TeleportOptions?, prompt: Boolean) {
        TODO("Not yet implemented")
    }

    override fun teleport(player: Player, destination: Player, teleportOptions: TeleportOptions?, prompt: Boolean) {
        TODO("Not yet implemented")
    }

    override fun teleport(player: Player, destination: Entity, teleportOptions: TeleportOptions?, prompt: Boolean) {
        TODO("Not yet implemented")
    }

    override suspend fun teleportSuspend(
        player: Player,
        destination: Location,
        teleportOptions: TeleportOptions?,
        prompt: Boolean
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun teleportSuspend(
        player: Player,
        destination: Player,
        teleportOptions: TeleportOptions?,
        prompt: Boolean
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun teleportSuspend(
        player: Player,
        destination: Entity,
        teleportOptions: TeleportOptions?,
        prompt: Boolean
    ) {
        TODO("Not yet implemented")
    }

    override fun tick() {
        TODO("Not yet implemented")
    }

}