package ink.pmc.essentials.teleport.random

import com.electronwill.nightconfig.core.Config
import ink.pmc.essentials.*
import ink.pmc.essentials.api.teleport.ManagerState
import ink.pmc.essentials.api.teleport.TeleportManager
import ink.pmc.essentials.api.teleport.random.*
import ink.pmc.essentials.api.teleport.random.CacheTaskState.*
import ink.pmc.essentials.config.EssentialsConfig
import ink.pmc.utils.chat.replace
import ink.pmc.utils.concurrent.async
import ink.pmc.utils.concurrent.submitAsync
import ink.pmc.utils.data.mapKv
import ink.pmc.utils.world.Pos2D
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Biome
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.math.BigDecimal
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration

class RandomTeleportManagerImpl : RandomTeleportManager, KoinComponent {

    private val baseConf by inject<EssentialsConfig>()
    private val conf = baseConf.RandomTeleport()
    private val teleportConf = baseConf.Teleport()
    private val teleport by inject<TeleportManager>()
    internal var currentTickCaches: Int = 0

    override val cacheTasks: MutableList<CacheTask> = mutableListOf()
    override val caches: MutableSet<RandomTeleportCache> = ConcurrentHashMap.newKeySet()
    override val maxChunkCachePerTick: Int = conf.chunkCacheMax
    override val maxCaches: Int = conf.chunkCacheMax
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
        cooldown = Duration.parse(conf.cooldown),
        cost = BigDecimal(conf.cost),
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
            cooldown = it.value.get<String>("cooldown")?.let { c -> Duration.parse(c) } ?: defaultOptions.cooldown,
            cost = it.value.get<String>("cost")?.let { c -> BigDecimal(c) } ?: defaultOptions.cost,
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
        return conf.chunkCacheAmount.get(world.name) ?: conf.chunkCacheDefaultAmount
    }

    override fun getCaches(world: World): Collection<RandomTeleportCache> {
        return caches.filter { it.world == world }
    }

    override fun pollCache(world: World): RandomTeleportCache? {
        if (!caches.any { it.world == world }) return null
        val cache = caches.firstOrNull { it.world == world } ?: return null
        cache.preservedChunks.forEach { it.removePluginChunkTicket(plugin) }
        caches.remove(cache)
        return cache
    }

    override fun pollCache(id: UUID): RandomTeleportCache? {
        if (!caches.any { it.id == id }) return null
        val cache = caches.firstOrNull { it.id == id } ?: return null
        cache.preservedChunks.forEach { it.removePluginChunkTicket(plugin) }
        caches.remove(cache)
        return cache
    }

    override suspend fun randomOnce(world: World, options: RandomTeleportOptions?): Location? {
        val opt = options ?: getRandomTeleportOptions(world)
        val range = opt.startRadius..opt.endRadius
        val center = getCenterLocation(world, options)
        val baseX = center.x.toInt()
        val baseZ = center.z.toInt()

        fun random(): Int {
            return range.random()
        }

        fun biome(location: Location): Boolean {
            return opt.blacklistedBiomes.contains(location.block.biome)
        }

        fun cover(location: Location): Boolean {
            for (y in location.blockY..(world.maxHeight - location.blockY)) {
                val loc = location.clone().add(0.0, y.toDouble(), 0.0)
                if (!loc.block.type.isAir) return true
            }
            return false
        }

        fun safeBlock(x: Int, z: Int): Location? {
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

        fun searchLocation(): Location? {
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
        cacheTasks.add(task)
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

    override suspend fun launchSuspend(
        player: Player,
        world: World,
        options: RandomTeleportOptions?,
        prompt: Boolean
    ) {
        async {
            val timer = TeleportTimer()
            val opt = options ?: getRandomTeleportOptions(world)
            val cache = if (options != null) pollCache(world) else null

            if (cache != null) {
                val location = cache.location
                teleport.teleport(player, location, prompt = prompt)
                val time = timer.end()
                if (prompt) {
                    player.sendMessage(
                        RANDOM_TELEPORT_SUCCED
                            .replace(
                                "<location>",
                                Component.text("${location.blockX}, ${location.blockY}, ${location.blockZ}")
                            )
                            .replace("<attempts>", Component.text(cache.attempts))
                            .replace("<time>", Component.text(time))
                    )
                }
                return@async
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
                return@async
            }

            teleport.teleport(player, location, prompt = prompt)
            val time = timer.end()

            if (prompt) {
                player.sendMessage(
                    RANDOM_TELEPORT_SUCCED
                        .replace(
                            "<location>",
                            Component.text("${location.blockX}, ${location.blockY}, ${location.blockZ}")
                        )
                        .replace("<attempts>", Component.text(attempts))
                        .replace("<time>", Component.text(time))
                )
            }
        }
    }

    override fun isEnabled(world: World): Boolean {
        return enabledWorlds.contains(world)
    }

    private suspend fun tickCacheTask() {
        currentTickCaches = 0
        cacheTasks.forEach {
            val cache = it.tick()
            when (it.state) {
                PENDING -> {}
                TICKING -> {}
                TICKING_CACHE -> {}

                SUCCEED -> {
                    if (cache != null) {
                        caches.add(cache)
                    }
                    cacheTasks.remove(it)
                }

                FAILED -> {
                    cacheTasks.remove(it)
                }

                CANCELLED -> {
                    cacheTasks.remove(it)
                }
            }
        }
        val worlds = Bukkit.getWorlds().filter { enabledWorlds.contains(it) }
        worlds.forEach {
            val cached = getCaches(it).size
            val limit = getCacheAmount(it)
            if (cached < limit) {
                submitCache(it)
            }
        }
    }

    override suspend fun tick() {
        if (hasUnfinishedTick) {
            return
        }

        if (cacheTasks.isEmpty()) {
            return
        }

        state = ManagerState.TICKING
        val start = System.currentTimeMillis()
        tickCacheTask()
        val end = System.currentTimeMillis()
        lastTickTime = end - start
        tickCount++
        state = ManagerState.IDLE
    }

}