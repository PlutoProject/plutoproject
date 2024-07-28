package ink.pmc.essentials.config

import com.electronwill.nightconfig.core.Config
import ink.pmc.utils.config.toMapViaEntry
import ink.pmc.utils.data.mapKv
import ink.pmc.utils.multiplaform.item.KeyedMaterial
import ink.pmc.utils.multiplaform.item.exts.bukkit
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.Biome

@Suppress("UNUSED")
class EssentialsConfig(private val config: Config) : Config by config {

    inner class Teleport {
        val enabled: Boolean get() = get("teleport.enabled")
        val maxRequestsStored: Int get() = get("teleport.max-requests-stored")
        val requestExpireAfter: String get() = get("teleport.request.expire-after")
        val requestRemoveAfter: String get() = get("teleport.request.remove-after")
        val queueProcessPerTick: Int get() = get("teleport.queue-process-per-tick")
        val chunkPrepareMethod: ChunkPrepareMethod get() = ChunkPrepareMethod.valueOf(get<String>("teleport.chunk-prepare-method").uppercase())
        val slowChunkPrepare: Boolean get() = get("teleport.slow-chunk-prepare")
        val avoidVoid: Boolean = get("teleport.avoid-void")
        val safeLocationSearchRadius: Int get() = get("teleport.safe-location-search-radius")
        val chunkPrepareRadius: Int get() = get("teleport.chunk-prepare-radius")
        val blacklistedBlocks: Collection<Material>
            get() = get<List<String>>("teleport.blacklisted-blocks").map { KeyedMaterial(it).bukkit }
        val worldOptions: Map<World, Config>
            get() = get<Config>("teleport.world-options")
                .toMapViaEntry().mapKv { Bukkit.getWorld(it.key)!! to it.value as Config }
        val blacklistedWorlds: Collection<World>
            get() = get<List<String>>("teleport.blacklisted-worlds").map { Bukkit.getWorld(it)!! }
    }

    inner class RandomTeleport {
        val enabled: Boolean get() = get("random-teleport.enabled")
        val spawnPointAsCenter: Boolean get() = get("random-teleport.spawnpoint-as-center")
        val centerX: Int get() = get("random-teleport.center.x")
        val centerZ: Int get() = get("random-teleport.center.z")
        val cacheProcessPerTick: Int get() = get("random-teleport.cache-process-per-tick")
        val chunkPreserveRadius: Int get() = get("random-teleport.chunk-preserve-radius")
        val startRadius: Int get() = get("random-teleport.start-radius")
        val endRadius: Int get() = get("random-teleport.end-radius")
        val minHeight: Int get() = get("random-teleport.min-height")
        val maxHeight: Int get() = get("random-teleport.max-height")
        val noCover: Boolean get() = get("random-teleport.no-cover")
        val maxRetries: Int get() = get("random-teleport.max-retries")
        val cooldown: Int get() = get("random-teleport.cooldown")
        val cost: Int get() = get("random-teleport.cost")
        val blacklistedBiomes: Collection<Biome>
            get() = get<List<String>>("random-teleport.blacklisted-biomes").map { Biome.valueOf(it.uppercase()) }
        val blacklistedBlocks: Collection<Material>
            get() = get<List<String>>("random-teleport.blacklisted-blocks").map { KeyedMaterial(it).bukkit }
        val worldOptions: Map<World, Config>
            get() = get<Config>("random-teleport.world-options")
                .toMapViaEntry().mapKv { Bukkit.getWorld(it.key)!! to it.value as Config }
        val blacklistedWorlds: Collection<World>
            get() = get<List<String>>("random-teleport.blacklisted-worlds").map { Bukkit.getWorld(it)!! }
    }

    inner class Back {
        val maxLocationStored: Int get() = get("back.max-location-stored")
        val blacklistedWorlds: Collection<World>
            get() = get<List<String>>("back.blacklisted-worlds").map { Bukkit.getWorld(it)!! }
    }

    inner class Home {
        val maxHomes: Int get() = get("home.max-homes")
        val blacklistedWorlds: Collection<World>
            get() = get<List<String>>("home.blacklisted-worlds").map { Bukkit.getWorld(it)!! }
    }

    inner class Warp {
        val blacklistedWorlds: Collection<World>
            get() = get<List<String>>("warp.blacklisted-worlds").map { Bukkit.getWorld(it)!! }
    }

    inner class Economy {
        val balanceTopEntries: Int get() = get("economy.balance-top-entries")
    }

    inner class Commands {
        operator fun get(name: String): Boolean {
            return config.get("commands.$name") ?: false
        }
    }

    inner class CommandAliases {
        operator fun get(name: String): Array<String> {
            return config.get<List<String>>("command-aliases.$name")?.toTypedArray() ?: arrayOf()
        }
    }

}