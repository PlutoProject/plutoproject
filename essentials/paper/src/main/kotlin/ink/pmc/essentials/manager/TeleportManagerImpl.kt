package ink.pmc.essentials.manager

import ink.pmc.essentials.*
import ink.pmc.essentials.api.teleport.*
import ink.pmc.essentials.api.teleport.TeleportDirection.COME
import ink.pmc.essentials.api.teleport.TeleportDirection.GO
import ink.pmc.utils.chat.DURATION
import ink.pmc.utils.chat.replace
import ink.pmc.utils.concurrent.async
import ink.pmc.utils.concurrent.submitAsync
import ink.pmc.utils.concurrent.submitSync
import ink.pmc.utils.concurrent.sync
import ink.pmc.utils.data.mapKv
import ink.pmc.utils.entity.teleportSuspend
import ink.pmc.utils.multiplaform.item.KeyedMaterial
import ink.pmc.utils.multiplaform.item.exts.bukkit
import ink.pmc.utils.world.ValueChunkLoc
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
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

    // 用于在完成队列任务时通知
    private val notifyChannel = MutableSharedFlow<Location>()

    override val teleportRequests: MutableList<TeleportRequest> = mutableListOf()
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
            val chunks = (-radius..radius).flatMap { x ->
                (-radius..radius).map { z ->
                    val x1 = centerChunk.x + x
                    val y1 = centerChunk.y + z
                    ValueChunkLoc(x1, y1)
                }
            }.toMutableList()
            chunks.add(centerChunk)
            return chunks
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
        val footCheck = block.type.isAir
        val headCheck = clone().add(0.0, 1.0, 0.0).block.type.isAir
        val stand = clone().subtract(0.0, 1.0, 0.0)
        val standCheck = !stand.block.type.isAir
        val blacklistCheck = !options.blacklistedBlocks.contains(stand.block.type)
        return voidCheck && footCheck && headCheck && standCheck && blacklistCheck
    }

    private fun Location.findSafeLoc(options: TeleportOptions): Location? {
        val visited = mutableSetOf<Location>()

        fun Location.bfs(): Location? {
            val radius = options.safeLocationSearchRadius
            val queue: Queue<Location> = LinkedList()
            queue.add(this)

            while (queue.isNotEmpty()) {
                val current = queue.poll()
                if (!visited.add(current)) {
                    continue
                }

                if (current.isSafe(options)) {
                    return current
                }

                // 添加相邻位置
                for (dx in -1..1) {
                    for (dy in -1..1) {
                        for (dz in -1..1) {
                            if (dx == 0 && dy == 0 && dz == 0) continue // 跳过当前位置
                            val neighbor = current.clone().add(dx.toDouble(), dy.toDouble(), dz.toDouble())
                            if (neighbor.distance(this) <= radius) {
                                queue.add(neighbor)
                            }
                        }
                    }
                }
            }

            return null
        }

        fun iterateLocations(range: IntRange): Location? {
            for (dy in range) {
                val loc = clone().subtract(0.0, dy.toDouble(), 0.0)

                if (loc.blockY < world.minHeight) {
                    return null
                }

                if (!loc.block.type.isAir) {
                    return loc
                }
            }
            return null
        }

        val startPoint = if (blockY < world.minHeight) {
            blockY
        } else {
            iterateLocations(0..(blockY - world.minHeight))?.blockY ?: blockY
        }

        for (dy in startPoint..world.maxHeight) {
            val loc = clone().apply { y = dy.toDouble() }
            val bfs = loc.bfs()
            if (bfs != null) {
                return bfs.toCenterLocation()
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
            async<Location?> { destination.findSafeLoc(options) }
        }

        if (loc == null) {
            if (prompt) {
                player.showTitle(TELEPORT_FAILED_TITLE)
                player.playSound(TELEPORT_FAILED_SOUND)
            }
            return
        }

        player.teleportSuspend(loc)

        if (prompt) {
            player.showTitle(TELEPORT_SUCCEED_TITLE)
            player.playSound(TELEPORT_SUCCEED_SOUND)
        }
    }

    override fun getRequest(id: UUID): TeleportRequest? {
        return teleportRequests.firstOrNull { it.id == id }
    }

    override fun getSentRequests(player: Player): Collection<TeleportRequest> {
        return teleportRequests.filter { it.source == player }
    }

    override fun getReceivedRequests(player: Player): Collection<TeleportRequest> {
        return teleportRequests.filter { it.destination == player }
    }

    override fun hasRequest(id: UUID): Boolean {
        return teleportRequests.any { it.id == id }
    }

    override fun hasUnfinishedRequest(player: Player): Boolean {
        return getSentRequests(player).any { !it.isFinished }
    }

    override fun getUnfinishedRequest(player: Player): TeleportRequest? {
        return getSentRequests(player).firstOrNull { !it.isFinished }
    }

    override fun hasPendingRequest(player: Player): Boolean {
        return getReceivedRequests(player).any { !it.isFinished }
    }

    override fun getPendingRequest(player: Player): TeleportRequest? {
        return getReceivedRequests(player).firstOrNull { !it.isFinished }
    }

    override fun createRequest(
        source: Player,
        destination: Player,
        direction: TeleportDirection,
        options: RequestOptions
    ): TeleportRequest? {
        if (hasUnfinishedRequest(source) || hasPendingRequest(destination)) {
            return null
        }

        if (conf.blacklistedWorlds.contains(destination.world)) {
            return null
        }

        val request = TeleportRequestImpl(options, source, destination, direction)
        val message = when (direction) {
            GO -> TELEPORT_TPA_RECEIVED.replace("<player>", source.name)
            COME -> TELEPORT_TPAHERE_RECEIVED.replace("<player>", source.name)
        }

        destination.sendMessage(message)
        destination.sendMessage(TELEPORT_EXPIRE.replace("<expire>", DURATION(options.expireAfter)))
        destination.sendMessage(TELEPORT_OPERATION(request.id))
        destination.playSound(TELEPORT_REQUEST_RECEIVED_SOUND)

        if (teleportRequests.size == conf.maxRequestsStored) {
            teleportRequests.removeAt(0).cancel()
        }

        teleportRequests.add(request)

        essentialsScope.submitAsync {
            delay(options.expireAfter)
            if (!hasRequest(request.id) || request.isFinished) {
                return@submitAsync
            }
            request.expire()
            destination.sendMessage(TELEPORT_REQUEST_EXPIRED.replace("<player>", source.name))
            destination.playSound(TELEPORT_REQUEST_CANCELLED_SOUND)
        }

        essentialsScope.submitAsync {
            delay(options.removeAfter)
            if (!hasRequest(request.id)) {
                return@submitAsync
            }
            removeRequest(request.id)
        }

        return request
    }

    override fun cancelRequest(id: UUID) {
        getRequest(id)?.cancel()
    }

    override fun cancelRequest(request: TeleportRequest) {
        request.cancel()
    }

    override fun removeRequest(id: UUID) {
        if (!hasRequest(id)) {
            return
        }
        getRequest(id)!!.cancel()
        teleportRequests.removeIf { it.id == id }
    }

    override fun clearRequest() {
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
            sync { fireTeleport(player, destination, teleportOptions, prompt) }
            return
        }

        if (prompt) {
            player.showTitle(TELEPORT_PREPARING_TITLE)
            player.playSound(TELEPORT_PREPARING_SOUND)
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