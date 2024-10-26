package ink.pmc.essentials.repositories

import com.github.benmanes.caffeine.cache.Caffeine
import com.mongodb.client.model.Filters.and
import com.mongodb.client.model.Filters.eq
import ink.pmc.essentials.config.EssentialsConfig
import ink.pmc.essentials.dtos.HomeDto
import ink.pmc.framework.provider.Provider
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toCollection
import org.bukkit.OfflinePlayer
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.Duration
import java.util.*

class HomeRepository : KoinComponent {
    private val conf by inject<EssentialsConfig>()
    private val cache = Caffeine.newBuilder()
        .expireAfterWrite(Duration.ofMinutes(10))
        .build<UUID, HomeDto>()
    private val db =
        Provider.defaultMongoDatabase.getCollection<HomeDto>("essentials_${conf.serverName}_homes")

    suspend fun findById(id: UUID): HomeDto? {
        val cached = cache.getIfPresent(id) ?: run {
            val lookup = db.find(eq("id", id.toString())).firstOrNull() ?: return null
            cache.put(id, lookup)
            lookup
        }
        return cached
    }

    suspend fun findByName(player: OfflinePlayer, name: String): HomeDto? {
        val cached = cache.asMap().values.firstOrNull { it.owner == player.uniqueId && it.name == name } ?: run {
            val lookup = db.find(
                and(eq("owner", player.uniqueId.toString()), eq("name", name))
            ).firstOrNull() ?: return null
            cache.put(lookup.id, lookup)
            lookup
        }
        return cached
    }

    suspend fun findByPlayer(player: OfflinePlayer): Collection<HomeDto> {
        return mutableListOf<HomeDto>().apply {
            db.find(eq("owner", player.uniqueId.toString())).toCollection(this)
        }
    }

    suspend fun hasById(id: UUID): Boolean {
        return findById(id) != null
    }

    suspend fun hasByName(player: OfflinePlayer, name: String): Boolean {
        return findByPlayer(player).any { it.name == name }
    }

    suspend fun deleteById(id: UUID) {
        cache.invalidate(id)
        db.deleteOne(eq("id", id.toString()))
    }

    suspend fun deleteByName(player: OfflinePlayer, name: String) {
        cache.asMap().entries.removeIf { it.value.owner == player.uniqueId && it.value.name == name }
        db.deleteOne(
            and(
                eq("owner", player.uniqueId.toString()),
                eq("name", name)
            )
        )
    }

    suspend fun save(dto: HomeDto) {
        require(!hasById(dto.id)) { "HomeDto with id ${dto.id} already existed" }
        db.insertOne(dto)
        cache.put(dto.id, dto)
    }

    suspend fun update(dto: HomeDto) {
        require(hasById(dto.id)) { "HomeDto with id ${dto.id} not exist" }
        db.replaceOne(eq("id", dto.id.toString()), dto)
    }
}