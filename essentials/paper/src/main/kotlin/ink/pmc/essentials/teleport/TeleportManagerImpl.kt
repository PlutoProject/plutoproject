package ink.pmc.essentials.teleport

import ink.pmc.essentials.*
import ink.pmc.essentials.api.teleport.*
import ink.pmc.essentials.api.teleport.TeleportDirection.COME
import ink.pmc.essentials.api.teleport.TeleportDirection.GO
import ink.pmc.essentials.config.ChunkPrepareMethod.*
import ink.pmc.essentials.config.EssentialsConfig
import ink.pmc.utils.chat.DURATION
import ink.pmc.utils.chat.UNUSUAL_ISSUE
import ink.pmc.utils.chat.replace
import ink.pmc.utils.concurrent.async
import ink.pmc.utils.concurrent.compose
import ink.pmc.utils.concurrent.submitAsync
import ink.pmc.utils.data.mapKv
import ink.pmc.utils.entity.teleportSuspend
import ink.pmc.utils.multiplaform.item.KeyedMaterial
import ink.pmc.utils.multiplaform.item.exts.bukkit
import ink.pmc.utils.world.ValueChunkLoc
import ink.pmc.utils.world.getChunkViaSource
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.future.asCompletableFuture
import kotlinx.coroutines.future.await
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.BlockFace
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.math.floor
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class TeleportManagerImpl : TeleportManager, KoinComponent {

    private val conf = get<EssentialsConfig>().Teleport()

    // 用于在完成队列任务时通知
    private val notifyChannel = MutableSharedFlow<UUID>()

    override val teleportRequests: MutableList<TeleportRequest> = mutableListOf()
    override val queue: Queue<TeleportTask> = ConcurrentLinkedQueue()
    override val defaultRequestOptions: RequestOptions = RequestOptions(
        Duration.parse(conf.requestExpireAfter),
        Duration.parse(conf.requestRemoveAfter)
    )
    override val defaultTeleportOptions: TeleportOptions = TeleportOptions(
        false,
        conf.avoidVoid,
        conf.safeLocationSearchRadius,
        conf.chunkPrepareRadius,
        conf.blacklistedBlocks.toSet(),
    )
    override val worldTeleportOptions: Map<World, TeleportOptions> = conf.worldOptions.mapKv {
        it.key to TeleportOptions(
            false,
            it.value.get("avoid-void") ?: defaultTeleportOptions.avoidVoid,
            it.value.get("safe-location-search-radius") ?: defaultTeleportOptions.safeLocationSearchRadius,
            it.value.get("chunk-prepare-radius") ?: defaultTeleportOptions.chunkPrepareRadius,
            it.value.get<List<String>>("blacklisted-blocks")?.map { m -> KeyedMaterial(m).bukkit }?.toSet()
                ?: defaultTeleportOptions.blacklistedBlocks
        )
    }
    override val blacklistedWorlds: Collection<World> = conf.blacklistedWorlds
    override var tickingTask: TeleportTask? = null
    override var tickCount = 0L
    override var lastTickTime = 0L
    override var state: TeleportManagerState = TeleportManagerState.IDLE

    override fun getWorldTeleportOptions(world: World): TeleportOptions {
        return world.teleportOptions
    }

    private val hasUnfinishedTick: Boolean
        get() = state == TeleportManagerState.TICKING

    private val World.teleportOptions: TeleportOptions
        get() = worldTeleportOptions[this] ?: defaultTeleportOptions

    private fun Location.chunkNeedToPrepare(radius: Int): List<ValueChunkLoc> {
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

    @JvmName("internalPrepareChunk")
    private suspend fun Collection<ValueChunkLoc>.prepareChunk(world: World) {
        coroutineScope {
            forEach {
                submitAsync {
                    when (conf.chunkPrepareMethod) {
                        SYNC -> world.getChunkAt(it.x, it.y)
                        ASYNC -> world.getChunkAtAsync(it.x, it.y).await()
                        ASYNC_SLOW -> world.getChunkAtAsyncUrgently(it.x, it.y).await()
                        CHUNK_SOURCE -> world.getChunkViaSource(it.x, it.y)
                    }
                    yield() // 确保任务超时可以正常取消
                }
            }
        }
    }

    @JvmName("internalIsSafe")
    private fun Location.isSafe(options: TeleportOptions): Boolean {
        val voidCheck = if (options.avoidVoid) y >= world.minHeight else true
        val footCheck = block.type.isAir
        val headCheck = clone().add(0.0, 1.0, 0.0).block.type.isAir
        val stand = clone().subtract(0.0, 1.0, 0.0)
        val standCheck = !stand.block.type.isAir
        val blacklistCheck = !options.blacklistedBlocks.contains(stand.block.type)
        val worldBorderCheck = this.world.worldBorder.isInside(this)
        return voidCheck && footCheck && headCheck && standCheck && blacklistCheck && worldBorderCheck
    }

    private suspend fun Location.searchSafeLoc(options: TeleportOptions): Location? {
        val visited = ConcurrentHashMap.newKeySet<Location>()

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

        suspend fun iterateLocations(range: IntProgression, direction: BlockFace): Location? {
            for (dy in range) {
                val loc = when (direction) {
                    BlockFace.UP -> clone().add(0.0, dy.toDouble(), 0.0)
                    BlockFace.DOWN -> clone().subtract(0.0, dy.toDouble(), 0.0)
                    else -> throw IllegalStateException("Unsupported direction")
                }

                if (direction == BlockFace.UP && loc.blockY > world.maxHeight) {
                    return null
                }

                if (direction == BlockFace.DOWN && loc.blockY < world.minHeight) {
                    return null
                }

                val bfs = loc.bfs()

                if (bfs != null) {
                    return bfs
                }

                yield() // 尽快响应取消
            }

            return null
        }

        val searchUp = submitAsync<Location?> { iterateLocations(0..(world.maxHeight - blockY), BlockFace.UP) }
        val searchCurr = submitAsync<Location?> { bfs() }
        val searchDown = submitAsync<Location?> { iterateLocations(0..(blockY - world.minHeight), BlockFace.DOWN) }
        val tasks = arrayOf(searchUp, searchCurr, searchDown)

        return compose(tasks) {
            it != null
        }?.toCenterLocation()?.apply { y = floor(y) }
    }

    override suspend fun fireTeleport(
        player: Player,
        destination: Location,
        options: TeleportOptions?,
        prompt: Boolean
    ) {
        val opt = options ?: destination.world.teleportOptions
        val loc = if (opt.bypassSafeCheck || destination.isSafe(opt)) {
            destination
        } else {
            async<Location?> { destination.searchSafeLoc(opt) }
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

    override fun getRequiredChunks(center: Location, radius: Int): Collection<ValueChunkLoc> {
        return center.chunkNeedToPrepare(radius)
    }

    override suspend fun prepareChunk(chunks: Collection<ValueChunkLoc>, world: World) {
        chunks.prepareChunk(world)
    }

    override fun teleport(player: Player, destination: Location, options: TeleportOptions?, prompt: Boolean) {
        submitAsync {
            teleportSuspend(player, destination, options, prompt)
        }
    }

    override fun teleport(player: Player, destination: Player, options: TeleportOptions?, prompt: Boolean) {
        submitAsync {
            teleportSuspend(player, destination.location, options, prompt)
        }
    }

    override fun teleport(player: Player, destination: Entity, options: TeleportOptions?, prompt: Boolean) {
        submitAsync {
            teleportSuspend(player, destination.location, options, prompt)
        }
    }

    override fun isSafe(location: Location, options: TeleportOptions?): Boolean {
        val opt = options ?: location.world.teleportOptions
        return location.isSafe(opt)
    }

    override suspend fun searchSafeLocationSuspend(start: Location, options: TeleportOptions?): Location? {
        val opt = options ?: start.world.teleportOptions
        return start.searchSafeLoc(opt)
    }

    override fun searchSafeLocation(start: Location, options: TeleportOptions?): Location? {
        return submitAsync<Location?> { searchSafeLocationSuspend(start, options) }.asCompletableFuture().join()
    }

    override suspend fun teleportSuspend(
        player: Player,
        destination: Location,
        options: TeleportOptions?,
        prompt: Boolean
    ) {
        async {
            val optRadius = destination.world.teleportOptions.chunkPrepareRadius
            val vt = player.sendViewDistance
            val radius = if (vt < optRadius) vt else optRadius
            val prepare = destination.chunkNeedToPrepare(radius)

            if (prepare.allPrepared(destination.world)) {
                fireTeleport(player, destination, options, prompt)
                return@async
            }

            if (prompt) {
                player.showTitle(TELEPORT_PREPARING_TITLE)
                player.playSound(TELEPORT_PREPARING_SOUND)
            }

            val id = UUID.randomUUID()
            val task = TeleportTaskImpl(id, player, destination, options, prompt, prepare)
            queue.offer(task)

            essentialsScope.submitAsync {
                delay(10.seconds)
                if (task.isFinished) {
                    return@submitAsync
                }
                task.cancel()
                player.showTitle(TELEPORT_FAILED_TIMEOUT_TITLE)
                player.sendMessage(UNUSUAL_ISSUE)
                player.playSound(TELEPORT_FAILED_SOUND)
            }

            coroutineScope {
                launch {
                    notifyChannel.collect {
                        if (it == id) {
                            cancel()
                        }
                    }
                }
            }
        }
    }

    override suspend fun teleportSuspend(
        player: Player,
        destination: Player,
        options: TeleportOptions?,
        prompt: Boolean
    ) {
        teleportSuspend(player, destination.location, options, prompt)
    }

    override suspend fun teleportSuspend(
        player: Player,
        destination: Entity,
        options: TeleportOptions?,
        prompt: Boolean
    ) {
        teleportSuspend(player, destination.location, options, prompt)
    }

    override fun isBlacklisted(world: World): Boolean {
        return blacklistedWorlds.contains(world)
    }

    override suspend fun tick() {
        if (hasUnfinishedTick) {
            return
        }

        if (queue.isEmpty()) {
            return
        }

        state = TeleportManagerState.TICKING
        val start = System.currentTimeMillis()

        repeat(conf.queueProcessPerTick) {
            val task = queue.poll() ?: return@repeat
            tickingTask = task
            task.tick()
            notifyChannel.emit(task.id)
            tickingTask = null
        }

        val end = System.currentTimeMillis()
        lastTickTime = end - start
        tickCount++
        state = TeleportManagerState.IDLE
    }

}