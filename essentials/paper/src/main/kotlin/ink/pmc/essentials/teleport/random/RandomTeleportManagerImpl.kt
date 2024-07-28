package ink.pmc.essentials.teleport.random

import com.electronwill.nightconfig.core.Config
import ink.pmc.essentials.api.teleport.ManagerState
import ink.pmc.essentials.api.teleport.random.*
import ink.pmc.essentials.config.EssentialsConfig
import ink.pmc.utils.data.mapKv
import ink.pmc.utils.world.Pos2D
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Biome
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import java.math.BigDecimal
import java.util.*
import kotlin.time.Duration

class RandomTeleportManagerImpl : RandomTeleportManager, KoinComponent {

    private val conf = get<EssentialsConfig>().RandomTeleport()

    override val cacheTasks: MutableList<RandomTeleportCacheTask> = mutableListOf()
    override val teleportQueue: Queue<RandomTeleportTask> = LinkedList()
    override val caches: MutableList<RandomTeleportCache> = mutableListOf()
    override val maxCaches: Int = conf.chunkCacheMax
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
    override val blacklistedWorlds: Collection<World> = conf.blacklistedWorlds
    override var tickCount: Long = 0L
    override var lastTickTime: Long = 0L
    override var state: ManagerState = ManagerState.IDLE

    override fun getRandomTeleportOptions(world: World): RandomTeleportOptions {
        return worldOptions[world] ?: defaultOptions
    }

    override fun getCenterLocation(world: World): Pos2D {
        val options = getRandomTeleportOptions(world)
        val spawnPoint = Pos2D(world.spawnLocation)
        val center = options.center
        val spawnPointAsCenter = options.spawnPointAsCenter
        return if (spawnPointAsCenter) spawnPoint else center
    }

    override fun pollCache(world: World): Location? {
        TODO("Not yet implemented")
    }

    override fun searchSafeLocation(world: World, options: RandomTeleportOptions?): Location? {
        TODO("Not yet implemented")
    }

    override fun launch(
        player: Player,
        world: World,
        options: RandomTeleportOptions?,
        prompt: Boolean
    ): RandomTeleportTask {
        TODO("Not yet implemented")
    }

    override suspend fun launchSuspend(
        player: Player,
        world: World,
        options: RandomTeleportOptions?,
        prompt: Boolean
    ): RandomTeleportTask {
        TODO("Not yet implemented")
    }

    override fun cancel(id: UUID) {
        TODO("Not yet implemented")
    }

    override fun cancel(player: Player) {
        TODO("Not yet implemented")
    }

    override fun isBlacklisted(world: World): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun tick() {
        TODO("Not yet implemented")
    }

}