package ink.pmc.essentials.warp

import ink.pmc.essentials.api.warp.Warp
import ink.pmc.essentials.api.warp.WarpManager
import ink.pmc.essentials.config.EssentialsConfig
import ink.pmc.essentials.dtos.WarpDto
import ink.pmc.essentials.repositories.WarpRepository
import ink.pmc.member.api.MemberService
import ink.pmc.utils.player.uuidOrNull
import ink.pmc.utils.storage.entity.dto
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

    override suspend fun getDefaultSpawn(): Warp? {
        return listSpawns().firstOrNull { it.isDefaultSpawn }
    }

    override suspend fun getPreferredSpawn(player: OfflinePlayer): Warp? {
        val member = MemberService.lookup(player.uniqueId) ?: error("Cannot obtain Member instance for ${player.name}")
        val dataContainer = member.dataContainer
        val spawnId = dataContainer.getString(PREFERRED_SPAWN_KEY)?.uuidOrNull ?: return null
        return get(spawnId)
    }

    override suspend fun setPreferredSpawn(player: OfflinePlayer, spawn: Warp) {
        val member = MemberService.lookup(player.uniqueId) ?: error("Cannot obtain Member instance for ${player.name}")
        val dataContainer = member.dataContainer
        dataContainer[PREFERRED_SPAWN_KEY] = spawn.id.toString()
        member.save()
    }

    override suspend fun list(): Collection<Warp> {
        val dto = repo.list()
        val homes = dto.mapNotNull { get(it.id) }
        return homes
    }

    override suspend fun listSpawns(): Collection<Warp> {
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

    override suspend fun unsetDefaultSpawn(spawn: Warp) {
        if (!spawn.isSpawn) return
        if (!spawn.isDefaultSpawn) return
        spawn.isDefaultSpawn = false
        spawn.update()
    }

    override suspend fun setDefaultSpawn(spawn: Warp) {
        if (!spawn.isSpawn) return
        if (spawn.isDefaultSpawn) return
        getDefaultSpawn()?.let { unsetDefaultSpawn(it) }
        spawn.isDefaultSpawn = true
        spawn.update()
    }

    override suspend fun create(name: String, location: Location, alias: String?): Warp {
        require(!has(name)) { "Warp named $name already existed" }
        val dto = WarpDto(
            objectId = ObjectId(),
            id = UUID.randomUUID(),
            name = name,
            alias = alias,
            createdAt = System.currentTimeMillis(),
            location = location.dto,
            isSpawn = false,
            isDefaultSpawn = false
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