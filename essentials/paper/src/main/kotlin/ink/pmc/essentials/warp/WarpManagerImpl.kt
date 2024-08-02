package ink.pmc.essentials.warp

import ink.pmc.essentials.api.warp.Warp
import ink.pmc.essentials.api.warp.WarpManager
import ink.pmc.essentials.config.EssentialsConfig
import ink.pmc.essentials.dtos.WarpDto
import ink.pmc.essentials.repositories.WarpRepository
import ink.pmc.utils.storage.entity.dto
import org.bson.types.ObjectId
import org.bukkit.Location
import org.bukkit.World
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class WarpManagerImpl : WarpManager, KoinComponent {

    private val conf by lazy { get<EssentialsConfig>().Warp() }
    private val repo by inject<WarpRepository>()

    override val blacklistedWorlds: Collection<World> = conf.blacklistedWorlds
    override val nameLengthLimit: Int = conf.nameLengthLimit
    override val loadedWarps: MutableMap<UUID, Warp> = ConcurrentHashMap()

    override fun isLoaded(id: UUID): Boolean {
        return loadedWarps.containsKey(id)
    }

    override fun isLoaded(name: String): Boolean {
        return loadedWarps.values.any { it.name == name }
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

    override suspend fun list(): Collection<Warp> {
        val dto = repo.list()
        val homes = dto.mapNotNull { get(it.id) }
        return homes
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
            System.currentTimeMillis(),
            location.dto,
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