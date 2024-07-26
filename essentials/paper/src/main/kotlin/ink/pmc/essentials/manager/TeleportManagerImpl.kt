package ink.pmc.essentials.manager

import ink.pmc.essentials.*
import ink.pmc.essentials.api.teleport.*
import ink.pmc.utils.concurrent.submitAsync
import ink.pmc.utils.concurrent.submitSync
import ink.pmc.utils.data.mapKv
import ink.pmc.utils.entity.teleportSuspend
import ink.pmc.utils.multiplaform.item.KeyedMaterial
import ink.pmc.utils.multiplaform.item.exts.bukkit
import ink.pmc.utils.world.ValueChunkLoc
import kotlinx.coroutines.flow.MutableSharedFlow
import org.bukkit.Location
import org.bukkit.Material
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
    // 用于在完成队列任务时通知
    private val notifyChannel = MutableSharedFlow<Location>()

    override val teleportRequests: MutableCollection<TeleportRequest> = mutableListOf()
    override val queue: Queue<TeleportTask> = ConcurrentLinkedQueue()
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

    private val Location.chunkNeedToPrepare: List<ValueChunkLoc>
        get() {
            val radius = world.teleportOptions.chunkPrepareRadius
            val centerChunk = ValueChunkLoc(chunk.x, chunk.z)
            return (-radius..radius).flatMap { x ->
                (-radius..radius).map { y ->
                    val x1 = centerChunk.x + x
                    val y1 = centerChunk.y + y
                    ValueChunkLoc(x1, y1)
                }
            }
        }

    private fun List<ValueChunkLoc>.allPrepared(world: World): Boolean {
        return all { it.isLoaded(world) }
    }

    private fun List<ValueChunkLoc>.prepareChunk(world: World) {
        forEach {
            it.getChunk(world)
        }
    }

    private fun Location.isSafe(options: TeleportOptions): Boolean {
        val voidCheck = if (options.avoidVoid) y >= world.minHeight else true
        val suffocateCheck = clone().add(0.0, 1.0, 0.0).block.type == Material.AIR
        val stand = clone().subtract(0.0, 1.0, 0.0)
        val standCheck = stand.block.type != Material.AIR
        val blacklistCheck = !options.blacklistedBlocks.contains(stand.block.type)
        return voidCheck && suffocateCheck && standCheck && blacklistCheck
    }

    private fun Location.findSafeLoc(options: TeleportOptions): Location? {
        for (x in -options.safeLocationSearchRadius..options.safeLocationSearchRadius) {
            for (y in -options.safeLocationSearchRadius..options.safeLocationSearchRadius) {
                for (z in -options.safeLocationSearchRadius..options.safeLocationSearchRadius) {
                    val loc = clone().add(x.toDouble(), y.toDouble(), z.toDouble())
                    if (loc.isSafe(options)) {
                        return loc
                    }
                }
            }
        }
        return null
    }

    private suspend fun fireTeleport(
        player: Player,
        destination: Location,
        teleportOptions: TeleportOptions?,
        prompt: Boolean = true
    ) {
        val options = teleportOptions ?: destination.world.teleportOptions
        val loc = if (destination.isSafe(options)) {
            destination
        } else {
            destination.findSafeLoc(options)
        }

        if (loc == null) {
            if (!prompt) {
                return
            }
            player.showTitle(TELEPORT_FAILED_TITLE)
            player.playSound(TELEPORT_FAILED_SOUND)
            return
        }

        player.teleportSuspend(loc)

        if (!prompt) {
            return
        }

        player.showTitle(TELEPORT_SUCCEED_TITLE)
        player.playSound(TELEPORT_SUCCEED_SOUND)
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
        if (!hasRequest(id)) {
            return
        }
        getRequest(id)!!.cancel()
        teleportRequests.removeIf { it.id == id }
    }

    override fun clearRequest(prompt: Boolean) {
        teleportRequests.forEach { it.cancel() }
        teleportRequests.clear()
    }

    override fun teleport(player: Player, destination: Location, teleportOptions: TeleportOptions?, prompt: Boolean) {
        submitAsync {
            teleportSuspend(player, destination, teleportOptions, prompt)
        }
    }

    override fun teleport(player: Player, destination: Player, teleportOptions: TeleportOptions?, prompt: Boolean) {
        submitAsync {
            teleportSuspend(player, destination.location, teleportOptions, prompt)
        }
    }

    override fun teleport(player: Player, destination: Entity, teleportOptions: TeleportOptions?, prompt: Boolean) {
        submitAsync {
            teleportSuspend(player, destination.location, teleportOptions, prompt)
        }
    }

    override suspend fun teleportSuspend(
        player: Player,
        destination: Location,
        teleportOptions: TeleportOptions?,
        prompt: Boolean
    ) {
        val prepare = destination.chunkNeedToPrepare

        if (prepare.allPrepared(destination.world)) {
            fireTeleport(player, destination, teleportOptions, prompt)
            return
        }

        queue.offer(TeleportTask(player, destination, teleportOptions, prompt, prepare))
        notifyChannel.collect {
            if (it == destination) {
                return@collect
            }
        }
    }

    override suspend fun teleportSuspend(
        player: Player,
        destination: Player,
        teleportOptions: TeleportOptions?,
        prompt: Boolean
    ) {
        teleportSuspend(player, destination.location, teleportOptions, prompt)
    }

    override suspend fun teleportSuspend(
        player: Player,
        destination: Entity,
        teleportOptions: TeleportOptions?,
        prompt: Boolean
    ) {
        teleportSuspend(player, destination.location, teleportOptions, prompt)
    }

    override fun tick() {
        val task = queue.poll() ?: return
        task.chunkNeedToPrepare.prepareChunk(task.destination.world)
        submitSync {
            fireTeleport(task.player, task.destination, task.teleportOptions, task.prompt)
            notifyChannel.emit(task.destination)
        }
    }

}