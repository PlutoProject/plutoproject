package ink.pmc.essentials.teleport.random

import com.electronwill.nightconfig.core.Config
import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.ListMultimap
import com.google.common.collect.Multimaps
import ink.pmc.essentials.*
import ink.pmc.essentials.api.teleport.ManagerState
import ink.pmc.essentials.api.teleport.TaskState.*
import ink.pmc.essentials.api.teleport.TeleportManager
import ink.pmc.essentials.api.teleport.random.*
import ink.pmc.essentials.config.EssentialsConfig
import ink.pmc.utils.chat.DURATION
import ink.pmc.utils.chat.currencyFormat
import ink.pmc.utils.chat.replace
import ink.pmc.utils.concurrent.async
import ink.pmc.utils.concurrent.submitAsync
import ink.pmc.utils.data.mapKv
import ink.pmc.utils.world.Pos2D
import ink.pmc.utils.world.addTicket
import ink.pmc.utils.world.removeTicket
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import net.kyori.adventure.text.Component
import net.minecraft.server.level.ChunkLevel
import net.minecraft.server.level.FullChunkStatus
import net.minecraft.server.level.TicketType
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Biome
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.LinkedBlockingDeque
import kotlin.time.toKotlinDuration

internal fun Chunk.addTeleportTicket() {
    if (hasTeleportTicket()) {
        return
    }
    addTicket(TicketType.PLUGIN_TICKET, x, z, ChunkLevel.byStatus(FullChunkStatus.FULL), plugin)
}

internal fun Chunk.removeTeleportTicket() {
    removeTicket(TicketType.PLUGIN_TICKET, x, z, ChunkLevel.byStatus(FullChunkStatus.FULL), plugin)
}

internal fun Chunk.hasTeleportTicket(): Boolean {
    return pluginChunkTickets.contains(plugin)
}

class RandomTeleportManagerImpl : RandomTeleportManager, KoinComponent {

    private val baseConf by inject<EssentialsConfig>()
    private val conf = baseConf.RandomTeleport()
    private val teleportConf = baseConf.Teleport()
    private val teleport by inject<TeleportManager>()
    private var waitedTicks: Long = -1
    private var inTeleport = ConcurrentHashMap.newKeySet<Player>()

    override val cacheTasks: Deque<CacheTask> = LinkedBlockingDeque()
    override val caches: ListMultimap<World, RandomTeleportCache> =
        Multimaps.synchronizedListMultimap(ArrayListMultimap.create())
    override val chunkPreserveRadius: Int =
        if (conf.chunkPreserveRadius >= 0) conf.chunkPreserveRadius else teleportConf.chunkPrepareRadius
    override val defaultOptions: RandomTeleportOptions = RandomTeleportOptions(
        center = Pos2D(conf.centerX, conf.centerZ),
        spawnPointAsCenter = conf.spawnPointAsCenter,
        startRadius = conf.startRadius,
        endRadius = conf.endRadius,
        maxHeight = conf.maxHeight,
        minHeight = conf.minHeight,
        noCover = conf.noCover,
        maxAttempts = conf.maxAttempts,
        cost = conf.cost,
        blacklistedBiomes = conf.blacklistedBiomes.toSet()
    )
    override val worldOptions: Map<World, RandomTeleportOptions> = conf.worldOptions.mapKv {
        it.key to RandomTeleportOptions(
            center = it.value.get<Config>("center")?.let { c -> Pos2D(c.get("x"), c.get("z")) }
                ?: defaultOptions.center,
            spawnPointAsCenter = it.value.get("spawnpoint-as-center") ?: defaultOptions.spawnPointAsCenter,
            startRadius = it.value.get("start-radius") ?: defaultOptions.startRadius,
            endRadius = it.value.get("end-radius") ?: defaultOptions.endRadius,
            maxHeight = it.value.get("max-height") ?: defaultOptions.maxHeight,
            minHeight = it.value.get("min-height") ?: defaultOptions.minHeight,
            noCover = it.value.get("no-cover") ?: defaultOptions.noCover,
            maxAttempts = it.value.get("max-attempts") ?: defaultOptions.maxAttempts,
            cost = it.value.get<Double>("cost") ?: defaultOptions.cost,
            blacklistedBiomes = it.value.get<List<String>>("blacklisted-biomes")
                ?.map { b -> Biome.valueOf(b.uppercase()) }?.toSet() ?: defaultOptions.blacklistedBiomes
        )
    }
    override val enabledWorlds: Collection<World> = conf.enabledWorlds
    override var tickCount: Long = 0L
    override var lastTickTime: Long = 0L
    override var state: ManagerState = ManagerState.IDLE

    private val hasUnfinishedTick: Boolean
        get() = state == ManagerState.TICKING

    override fun getRandomTeleportOptions(world: World): RandomTeleportOptions {
        return worldOptions[world] ?: defaultOptions
    }

    override fun getCenterLocation(world: World, options: RandomTeleportOptions?): Pos2D {
        val opt = options ?: getRandomTeleportOptions(world)
        val spawnPoint = Pos2D(world.spawnLocation)
        val center = opt.center
        val spawnPointAsCenter = opt.spawnPointAsCenter
        return if (spawnPointAsCenter) spawnPoint else center
    }

    override fun getCacheAmount(world: World): Int {
        return conf.cacheAmount.get(world.name) ?: conf.cacheDefaultAmount
    }

    override fun getCaches(world: World): Collection<RandomTeleportCache> {
        return caches.get(world)
    }

    override fun pollCache(world: World): RandomTeleportCache? {
        if (getCaches(world).isEmpty()) return null
        val cache = caches.get(world).removeFirst()
        cache.preservedChunks.forEach { it.removeTeleportTicket() }
        return cache
    }

    override suspend fun randomOnce(world: World, options: RandomTeleportOptions?): Location? {
        val opt = options ?: getRandomTeleportOptions(world)
        val rad = opt.endRadius - opt.startRadius
        val range = -rad..rad
        val center = getCenterLocation(world, options)
        val baseX = center.x.toInt()
        val baseZ = center.z.toInt()

        require(opt.startRadius < opt.endRadius) { "startRadius must less than endRadius" }

        fun random(): Int {
            val rand = range.random()
            val offset = if (rand >= 0) opt.startRadius else -opt.startRadius
            return rand + offset
        }

        fun biome(location: Location): Boolean {
            return opt.blacklistedBiomes.contains(location.block.biome)
        }

        fun cover(location: Location): Boolean {
            for (y in 0..(world.maxHeight - location.blockY)) {
                val loc = location.clone().add(0.0, y.toDouble(), 0.0)
                if (!loc.block.type.isAir) return true
            }
            return false
        }

        suspend fun safeBlock(x: Int, z: Int): Location? {
            for (y in world.maxHeight downTo world.minHeight) {
                val stand = Location(world, x.toDouble(), y.toDouble(), z.toDouble())
                val loc = stand.clone().add(0.0, 1.0, 0.0)

                if (stand.block.type.isAir) continue
                if (biome(loc)) continue
                if (!teleport.isSafe(loc)) continue
                if (opt.noCover && cover(loc)) continue

                return stand
            }

            return null
        }

        suspend fun searchLocation(): Location? {
            val dx = baseX + random()
            val dz = baseZ + random()
            return safeBlock(dx, dz)
        }

        return async<Location?> { searchLocation() }
    }

    override suspend fun random(world: World, options: RandomTeleportOptions?): RandomResult {
        val opt = options ?: getRandomTeleportOptions(world)
        return async<RandomResult> {
            var attempts = 0
            repeat(opt.maxAttempts) {
                attempts++
                val loc = randomOnce(world, opt)
                if (loc != null) return@async RandomResult(attempts, loc)
            }
            return@async RandomResult(attempts, null)
        }
    }

    override fun submitCache(world: World, options: RandomTeleportOptions?): CacheTask {
        val opt = options ?: getRandomTeleportOptions(world)
        val task = CacheTaskImpl(world, opt)
        cacheTasks.offer(task)
        return task
    }

    override fun submitCacheFirst(world: World, options: RandomTeleportOptions?): CacheTask {
        val opt = options ?: getRandomTeleportOptions(world)
        val task = CacheTaskImpl(world, opt)
        cacheTasks.offerFirst(task)
        return task
    }

    override fun hasCacheTask(id: UUID): Boolean {
        return cacheTasks.any { it.id == id }
    }

    override fun launch(
        player: Player,
        world: World,
        options: RandomTeleportOptions?,
        prompt: Boolean
    ) {
        submitAsync {
            launchSuspend(player, world, options, prompt)
        }
    }

    private class TeleportTimer {

        private val start: Long = System.currentTimeMillis()
        private var end: Long? = null

        fun end(): Long {
            end = System.currentTimeMillis()
            return end!! - start
        }

    }

    private fun notifyPlayerOfTeleport(
        player: Player,
        location: Location,
        attempts: Int,
        time: Long,
        costed: Boolean,
        cost: Double,
        symbol: String,
        prompt: Boolean
    ) {
        if (!prompt) {
            return
        }
        val message = if (!costed) RANDOM_TELEPORT_SUCCED else RANDOM_TELEPORT_SUCCED_COST
        player.sendMessage(
            message
                .replace(
                    "<location>",
                    Component.text("${location.blockX}, ${location.blockY}, ${location.blockZ}")
                )
                .replace("<amount>", cost.currencyFormat())
                .replace("<symbol>", symbol)
                .replace("<attempts>", Component.text(attempts))
                .replace("<lastLookupTime>", DURATION(java.time.Duration.ofMillis(time).toKotlinDuration()))
        )
    }

    override suspend fun launchSuspend(
        player: Player,
        world: World,
        options: RandomTeleportOptions?,
        prompt: Boolean
    ) {
        async {
            if (inTeleport.contains(player)) {
                if (prompt) {
                    player.sendMessage(RANDOM_TELEPORT_FAILED_IN_PROGRESS)
                }
                return@async
            }

            val defaultOpt = getRandomTeleportOptions(world)
            val opt = options ?: defaultOpt

            val eco = economy!!
            val plural = eco.currencyNamePlural() ?: DEFAULT_ECONOMY_SYMBOL
            val singular = eco.currencyNameSingular() ?: DEFAULT_ECONOMY_SYMBOL
            val cost = opt.cost
            val symbol = if (cost <= 1) singular else plural
            var costed = false

            if (economy != null && cost > 0.0 && !player.hasPermission(RANDOM_TELEPORT_COST_BYPASS)) {
                val balance = eco.getBalance(player)
                if (balance < cost) {
                    player.sendMessage(
                        RANDOM_TELEPORT_BALANCE_NOT_ENOUGH
                            .replace("<amount>", cost.currencyFormat())
                            .replace("<symbol>", symbol)
                            .replace("<balance>", balance.currencyFormat())
                    )
                    player.playSound(TELEPORT_FAILED_SOUND)
                    return@async
                }
                eco.withdrawPlayer(player, cost)
                costed = true
            }

            val cache = if (opt == defaultOpt) pollCache(world) else null
            val timer = TeleportTimer()
            inTeleport.add(player)

            if (cache != null) {
                val location = cache.location
                // 必须异步触发
                val event = RandomTeleportEvent(player, player.location, location).apply { callEvent() }
                if (event.isCancelled) return@async
                teleport.teleportSuspend(player, location, prompt = prompt)
                val time = timer.end()
                notifyPlayerOfTeleport(player, location, cache.attempts, time, costed, cost, symbol, prompt)
                inTeleport.remove(player)
                return@async
            }

            if (prompt) {
                player.showTitle(RANDOM_TELEPORT_SEARCHING_TITLE)
                player.playSound(RANDOM_TELEPORT_SEARCHING_SOUND)
            }

            val random = random(world, opt)
            val attempts = random.attempts
            val location = random.location

            if (location == null) {
                if (prompt) {
                    player.showTitle(TELEPORT_FAILED_TITLE)
                    player.playSound(TELEPORT_FAILED_SOUND)
                    player.sendMessage(RANDOM_TELEPORT_SEARCHING_FAILED)
                }
                inTeleport.remove(player)
                return@async
            }

            // 必须异步触发
            val event = RandomTeleportEvent(player, player.location, location).apply { callEvent() }
            if (event.isCancelled) return@async

            teleport.teleportSuspend(player, location, prompt = prompt)
            val time = timer.end()
            notifyPlayerOfTeleport(player, location, attempts, time, costed, cost, symbol, prompt)
            inTeleport.remove(player)
        }
    }

    override fun isEnabled(world: World): Boolean {
        return enabledWorlds.contains(world)
    }

    private suspend fun tickCacheTask() {
        val task = cacheTasks.poll() ?: return
        val cache = task.tick()
        when (task.state) {
            PENDING -> {}
            TICKING -> {}
            FINISHED -> {
                if (cache != null) {
                    caches.put(cache.world, cache)
                }
            }
        }
    }

    private suspend fun refreshChunkCache() = supervisorScope {
        caches.forEach { _, it ->
            val preserve = teleport.getRequiredChunks(it.location, chunkPreserveRadius)
            if (preserve.all { c -> c.isLoaded(it.world) && c.getChunk(it.world).hasTeleportTicket() }) {
                return@forEach
            }
            launch {
                teleport.prepareChunk(preserve, it.world)
                preserve.forEach { c -> c.getChunk(it.world).addTeleportTicket() }
            }
        }
    }

    private fun tryEmitTasks() {
        enabledWorlds.forEach {
            val amount = getCacheAmount(it)
            val pending = cacheTasks.filter { t -> t.world == it }.size
            val spare = amount - (getCaches(it).size + pending)

            if (spare <= 0) {
                return@forEach
            }

            repeat(spare) { _ ->
                submitCacheFirst(it, getRandomTeleportOptions(it))
            }
        }
    }

    override suspend fun tick() {
        if (hasUnfinishedTick) {
            return
        }

        if (waitedTicks != -1L && waitedTicks < conf.cacheInterval) {
            waitedTicks++
            return
        }

        tryEmitTasks()

        if (cacheTasks.isEmpty()) {
            return
        }

        state = ManagerState.TICKING
        val start = System.currentTimeMillis()

        tickCacheTask()
        refreshChunkCache()

        val end = System.currentTimeMillis()
        lastTickTime = end - start
        tickCount++
        state = ManagerState.IDLE
        waitedTicks = 0
    }

}