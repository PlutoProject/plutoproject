package ink.pmc.essentials.warp

import com.sksamuel.aedile.core.cacheBuilder
import ink.pmc.essentials.api.warp.Warp
import ink.pmc.essentials.api.warp.WarpCategory
import ink.pmc.essentials.api.warp.WarpManager
import ink.pmc.essentials.api.warp.WarpType
import ink.pmc.essentials.config.EssentialsConfig
import ink.pmc.essentials.models.WarpModel
import ink.pmc.essentials.repositories.WarpRepository
import ink.pmc.framework.playerdb.PlayerDb
import ink.pmc.framework.utils.platform.paper
import ink.pmc.framework.utils.player.uuidOrNull
import ink.pmc.framework.utils.storage.model
import org.bson.types.ObjectId
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.World
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import java.util.*
import java.util.concurrent.ConcurrentMap
import kotlin.time.Duration

private const val PREFERRED_SPAWN_KEY = "essentials.warp.preferred_spawn"

class WarpManagerImpl : WarpManager, KoinComponent {
    private val config by lazy { get<EssentialsConfig>().warp }
    private val repo by inject<WarpRepository>()
    private val cache = cacheBuilder<UUID, Warp> {
        refreshAfterWrite = Duration.parse("5m")
    }.build()

    override val blacklistedWorlds: Collection<World> = config.blacklistedWorlds
        .filter { name -> paper.worlds.any { it.name == name } }
        .map { paper.getWorld(it)!! }
    override val nameLengthLimit: Int = config.nameLengthLimit

    private suspend fun isCached(name: String): Boolean {
        return cache.asMap().values.any { it.name == name }
    }

    private suspend fun invalidate(name: String) {
        (cache.asMap() as ConcurrentMap).entries.removeIf { it.value.name == name }
    }

    override suspend fun get(id: UUID): Warp? {
        return cache.getOrNull(id) ?: repo.findById(id)?.let {
            WarpImpl(it).also { warp -> cache.put(id, warp) }
        }
    }

    override suspend fun get(name: String): Warp? {
        return cache.asMap().values.firstOrNull { it.name == name }
            ?: repo.findByName(name)?.let {
                WarpImpl(it).also { warp -> cache.put(warp.id, warp) }
            }
    }

    override suspend fun getSpawn(id: UUID): Warp? {
        return listSpawns().firstOrNull { it.id == id }
    }

    override suspend fun getSpawn(name: String): Warp? {
        return listSpawns().firstOrNull { it.name == name }
    }

    override suspend fun setSpawn(warp: Warp, spawn: Boolean) {
        warp.type = when {
            spawn && !warp.isSpawn -> WarpType.SPAWN
            !spawn && warp.isSpawn -> WarpType.WARP
            else -> return
        }
        warp.update()
    }

    override suspend fun getDefaultSpawn(): Warp? {
        return listSpawns().firstOrNull { it.isDefaultSpawn }
    }

    override suspend fun setDefaultSpawn(warp: Warp, default: Boolean) {
        getDefaultSpawn()?.also {
            if (it == warp) return@also
            setDefaultSpawn(it, false)
        }
        warp.type = when {
            default && !warp.isDefaultSpawn -> WarpType.SPAWN_DEFAULT
            !default && warp.isDefaultSpawn -> WarpType.SPAWN
            else -> return
        }
        warp.update()
    }

    override suspend fun getPreferredSpawn(player: OfflinePlayer): Warp? {
        val database = PlayerDb.getOrCreate(player.uniqueId)
        val spawnId = database.getString(PREFERRED_SPAWN_KEY)?.uuidOrNull ?: return getDefaultSpawn()
        val spawn = get(spawnId) ?: return null
        return if (spawn.isSpawn) spawn else null
    }

    override suspend fun setPreferredSpawn(player: OfflinePlayer, spawn: Warp) {
        require(spawn.isSpawn) { "Warp ${spawn.name} isn't a spawn" }
        val database = PlayerDb.getOrCreate(player.uniqueId)
        database[PREFERRED_SPAWN_KEY] = spawn.id.toString()
        database.update()
    }

    override suspend fun list(): Collection<Warp> {
        return repo.find().map {
            WarpImpl(it).also { warp -> cache.put(warp.id, warp) }
        }
    }

    override suspend fun listSpawns(): List<Warp> {
        return repo.findSpawns().map {
            WarpImpl(it).also { warp -> cache.put(warp.id, warp) }
        }
    }

    override suspend fun listByCategory(category: WarpCategory): Collection<Warp> {
        return repo.findByCategory(category).map {
            WarpImpl(it).also { warp -> cache.put(warp.id, warp) }
        }
    }

    override suspend fun has(id: UUID): Boolean {
        if (cache.contains(id)) return true
        return repo.hasById(id)
    }

    override suspend fun has(name: String): Boolean {
        if (isCached(name)) return true
        return repo.hasByName(name)
    }

    override suspend fun create(
        name: String,
        location: Location,
        alias: String?,
        icon: Material?,
        category: WarpCategory?
    ): Warp {
        require(!has(name)) { "Warp named $name already existed" }
        val model = WarpModel(
            ObjectId(),
            UUID.randomUUID(),
            name,
            alias,
            icon,
            category,
            WarpType.WARP,
            System.currentTimeMillis(),
            location.model,
        )
        val warp = WarpImpl(model)
        cache.put(model.id, warp)
        repo.save(model)
        return warp
    }

    override suspend fun remove(id: UUID) {
        cache.invalidate(id)
        repo.deleteById(id)
    }

    override suspend fun remove(name: String) {
        invalidate(name)
        repo.deleteByName(name)
    }

    override suspend fun update(warp: Warp) {
        warp.update()
    }

    override fun isBlacklisted(world: World): Boolean {
        return blacklistedWorlds.contains(world)
    }
}