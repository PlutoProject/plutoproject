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
import kotlin.time.Duration

@Suppress("UNUSED")
class EssentialsConfig(private val config: Config) : Config by config {

    val serverName: String get() = get("server-name")

    inner class Teleport {
        val enabled: Boolean get() = get("teleport.enabled")
        val maxRequestsStored: Int get() = get("teleport.max-requests-stored")
        val requestExpireAfter: String get() = get("teleport.request.expire-after")
        val requestRemoveAfter: String get() = get("teleport.request.remove-after")
        val queueProcessPerTick: Int get() = get("teleport.queue-process-per-tick")
        val chunkPrepareMethod: ChunkPrepareMethod get() = ChunkPrepareMethod.valueOf(get<String>("teleport.chunk-prepare-method").uppercase())
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
        val centerX: Double get() = get("random-teleport.center.x")
        val centerZ: Double get() = get("random-teleport.center.z")
        val cacheInterval: Long get() = get("random-teleport.cache.interval")
        val cacheDefaultAmount: Int get() = get("random-teleport.cache.amount-default")
        val cacheAmount: Config get() = get("random-teleport.cache.amount")
        val chunkPreserveRadius: Int get() = get("random-teleport.chunk-preserve-radius")
        val startRadius: Int get() = get("random-teleport.start-radius")
        val endRadius: Int get() = get("random-teleport.end-radius")
        val maxHeight: Int get() = get("random-teleport.max-height")
        val minHeight: Int get() = get("random-teleport.min-height")
        val noCover: Boolean get() = get("random-teleport.no-cover")
        val maxAttempts: Int get() = get("random-teleport.max-attempts")
        val cooldown: String get() = get("random-teleport.cooldown")
        val cost: Double get() = get("random-teleport.cost")
        val blacklistedBiomes: Collection<Biome>
            get() = get<List<String>>("random-teleport.blacklisted-biomes").map { Biome.valueOf(it.uppercase()) }
        val enabledWorlds: Collection<World>
            get() = get<List<String>>("random-teleport.enabled-worlds").map { Bukkit.getWorld(it)!! }
        val worldOptions: Map<World, Config>
            get() = get<Config>("random-teleport.world-options")
                .toMapViaEntry().mapKv { Bukkit.getWorld(it.key)!! to it.value as Config }
    }

    inner class Back {
        val enabled: Boolean get() = get("back.enabled")
        val maxLocations: Int get() = get("back.max-locations")
        val blacklistedWorlds: Collection<World>
            get() = get<List<String>>("back.blacklisted-worlds").map { Bukkit.getWorld(it)!! }
    }

    inner class Home {
        val enabled: Boolean get() = get("home.enabled")
        val maxHomes: Int get() = get("home.max-homes")
        val nameLengthLimit: Int get() = get("home.name-length-limit")
        val blacklistedWorlds: Collection<World>
            get() = get<List<String>>("home.blacklisted-worlds").map { Bukkit.getWorld(it)!! }
    }

    inner class Warp {
        val enabled: Boolean get() = get("warp.enabled")
        val nameLengthLimit: Int get() = get("warp.name-length-limit")
        val blacklistedWorlds: Collection<World>
            get() = get<List<String>>("warp.blacklisted-worlds").map { Bukkit.getWorld(it)!! }
        val spawns: Collection<String> get() = get<List<String>>("warp.spawns")
    }

    inner class Afk {
        val enabled: Boolean get() = get("afk.enabled")
        val idleDuration: Duration get() = Duration.parse(get("afk.idle-duration"))
    }

    inner class ItemFrame {
        val enabled: Boolean get() = get("itemframe.enabled")
    }

    inner class Lectern {
        val enabled: Boolean get() = get("lectern.enabled")
    }

    inner class Action {
        val enabled: Boolean get() = get("action.enabled")
        val sneakSwapMenu: Boolean get() = get("action.sneak-swap-menu")
    }

    inner class Item {
        val enabled: Boolean get() = get("item.enabled")
        val menu: Boolean get() = get("item.menu")
    }

    inner class Recipe {
        val enabled: Boolean get() = get("recipe.enabled")
        val menuItem: Boolean get() = get("recipe.menu-item")
    }

    inner class Join {
        val enabled: Boolean get() = get("join.enabled")
        val menuItem: Boolean get() = get("join.menu-item")
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

    inner class WorldAliases {
        operator fun get(world: World): String {
            return config.get<String>("world-aliases.${world.name}") ?: world.name
        }
    }

}