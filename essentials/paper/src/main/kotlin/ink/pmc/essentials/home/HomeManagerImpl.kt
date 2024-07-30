package ink.pmc.essentials.home

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.ListMultimap
import com.google.common.collect.Multimaps
import ink.pmc.essentials.api.home.Home
import ink.pmc.essentials.api.home.HomeManager
import ink.pmc.essentials.config.EssentialsConfig
import ink.pmc.essentials.disabled
import ink.pmc.essentials.dtos.HomeDto
import ink.pmc.essentials.essentialsScope
import ink.pmc.essentials.repositories.HomeRepository
import ink.pmc.utils.concurrent.submitAsync
import ink.pmc.utils.storage.entity.dto
import kotlinx.coroutines.delay
import org.bson.types.ObjectId
import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.bukkit.World
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import java.util.*
import kotlin.time.Duration.Companion.minutes

internal fun loadFailed(id: UUID, reason: String): String {
    return "Failed to loadAll Home $id: $reason"
}

class HomeManagerImpl : HomeManager, KoinComponent {

    private val conf by lazy { get<EssentialsConfig>().Home() }
    private val repo by inject<HomeRepository>()

    override val maxHomes: Int = conf.maxHomes
    override val blacklistedWorlds: Collection<World> = conf.blacklistedWorlds
    override val loadedHomes: ListMultimap<OfflinePlayer, Home> =
        Multimaps.synchronizedListMultimap<OfflinePlayer, Home>(ArrayListMultimap.create())

    init {
        essentialsScope.submitAsync {
            while (!disabled) {
                delay(5.minutes)
                loadedHomes.entries().removeIf { !it.key.isOnline }
            }
        }
    }

    override fun isLoaded(id: UUID): Boolean {
        return loadedHomes.values().any { it.id == id }
    }

    override fun unload(id: UUID) {
        loadedHomes.values().removeIf { it.id == id  }
    }

    override fun unloadAll(player: OfflinePlayer) {
        loadedHomes.removeAll(player)
    }

    override suspend fun get(id: UUID): Home? {
        val dto = repo.findById(id) ?: return null
        val home = HomeImpl(dto)
        if (!isLoaded(id)) {
            loadedHomes.put(home.owner, home)
        }
        return home
    }

    override suspend fun get(player: OfflinePlayer, name: String): Home? {
        val dto = repo.findByName(player, name) ?: return null
        val home = HomeImpl(dto)
        if (!isLoaded(dto.id)) {
            loadedHomes.put(home.owner, home)
        }
        return home
    }

    override suspend fun list(player: OfflinePlayer): Collection<Home> {
        val dto = repo.findByPlayer(player)
        val homes = dto.map { HomeImpl(it) }
        homes.forEach {
            if (!isLoaded(it.id)) {
                loadedHomes.put(it.owner, it)
            }
        }
        return homes
    }

    override suspend fun has(player: OfflinePlayer, name: String): Boolean {
        return repo.hasByName(player, name)
    }

    override suspend fun remove(id: UUID) {
        repo.deleteById(id)
    }

    override suspend fun remove(player: OfflinePlayer, name: String) {
        repo.deleteByName(player, name)
    }

    override suspend fun create(owner: Player, name: String, location: Location): Home {
        val dto = HomeDto(
            ObjectId(),
            UUID.randomUUID(),
            name,
            System.currentTimeMillis(),
            location.dto,
            owner.uniqueId
        )
        val home = HomeImpl(dto)
        loadedHomes.put(owner, home)
        repo.save(dto)
        return home
    }

    override suspend fun update(home: Home) {
        home.update()
    }

    override fun isBlacklisted(world: World): Boolean {
        return blacklistedWorlds.contains(world)
    }

}