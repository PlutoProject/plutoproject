package ink.pmc.essentials.warp

import ink.pmc.essentials.api.warp.Warp
import ink.pmc.essentials.api.warp.WarpManager
import ink.pmc.essentials.api.warp.WarpType
import ink.pmc.essentials.config.EssentialsConfig
import ink.pmc.essentials.dtos.WarpDto
import ink.pmc.essentials.repositories.WarpRepository
import ink.pmc.framework.playerdb.PlayerDb
import ink.pmc.framework.utils.concurrent.submitAsync
import ink.pmc.framework.utils.player.uuidOrNull
import ink.pmc.framework.utils.storage.model
import org.bson.types.ObjectId
import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.bukkit.World
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import java.util.*
import java.util.concurrent.ConcurrentHashMap

private const val PREFERRED_SPAWN_KEY = "essentials.warp.preferred_spawn"

class WarpManagerImpl : WarpManager, KoinComponent {

    private val conf by lazy { get<EssentialsConfig>().Warp() }
    private val repo by inject<WarpRepository>()

    override val blacklistedWorlds: Collection<World> = conf.blacklistedWorlds
    override val nameLengthLimit: Int = conf.nameLengthLimit
    override val loadedWarps: MutableMap<UUID, Warp> = ConcurrentHashMap()

    init {
        // 加载所有 Warp
        submitAsync {
            list()
        }
    }

    override fun isLoaded(id: UUID): Boolean {
        return getLoaded(id) != null
    }

    override fun isLoaded(name: String): Boolean {
        return getLoaded(name) != null
    }

    override fun unload(id: UUID) {
        loadedWarps.remove(id)
    }

    override fun unload(name: String) {
        loadedWarps.entries.removeIf { it.value.name == name }
    }

    override fun unloadAll() {
        loadedWarps.clear()
    }

    private fun getLoaded(id: UUID): Warp? {
        return loadedWarps[id]
    }

    private fun getLoaded(name: String): Warp? {
        return loadedWarps.values.firstOrNull { it.name == name }
    }

    override suspend fun get(id: UUID): Warp? {
        val loaded = getLoaded(id) ?: run {
            val dto = repo.findById(id) ?: return null
            val warp = WarpImpl(dto)
            loadedWarps[id] = warp
            warp
        }
        return loaded
    }

    override suspend fun get(name: String): Warp? {
        val loaded = getLoaded(name) ?: run {
            val dto = repo.findByName(name) ?: return null
            val warp = WarpImpl(dto)
            loadedWarps[dto.id] = warp
            warp
        }
        return loaded
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
        val dto = repo.list()
        val homes = dto.mapNotNull { get(it.id) }
        return homes
    }

    override suspend fun listSpawns(): List<Warp> {
        return list().filter { it.isSpawn }
    }

    override suspend fun has(id: UUID): Boolean {
        if (isLoaded(id)) return true
        return repo.hasById(id)
    }

    override suspend fun has(name: String): Boolean {
        if (isLoaded(name)) return true
        return repo.hasByName(name)
    }

    override suspend fun create(name: String, location: Location, alias: String?): Warp {
        require(!has(name)) { "Warp named $name already existed" }
        val dto = WarpDto(
            ObjectId(),
            UUID.randomUUID(),
            name,
            alias,
            WarpType.WARP,
            System.currentTimeMillis(),
            location.model,
        )
        val warp = WarpImpl(dto)
        loadedWarps[dto.id] = warp
        repo.save(dto)
        return warp
    }

    override suspend fun remove(id: UUID) {
        if (isLoaded(id)) unload(id)
        repo.deleteById(id)
    }

    override suspend fun remove(name: String) {
        if (isLoaded(name)) unload(name)
        repo.deleteByName(name)
    }

    override suspend fun update(warp: Warp) {
        warp.update()
    }

    override fun isBlacklisted(world: World): Boolean {
        return blacklistedWorlds.contains(world)
    }

}