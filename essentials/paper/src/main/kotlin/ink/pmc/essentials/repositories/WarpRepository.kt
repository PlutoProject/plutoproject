package ink.pmc.essentials.repositories

import com.github.benmanes.caffeine.cache.Caffeine
import com.mongodb.client.model.Filters.eq
import ink.pmc.essentials.config.EssentialsConfig
import ink.pmc.essentials.dtos.WarpDto
import ink.pmc.framework.provider.Provider
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toCollection
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.Duration
import java.util.*

class WarpRepository : KoinComponent {
    private val conf by inject<EssentialsConfig>()
    private val cache = Caffeine.newBuilder()
        .expireAfterWrite(Duration.ofMinutes(10))
        .build<UUID, WarpDto>()
    private val db =
        Provider.defaultMongoDatabase.getCollection<WarpDto>("essentials_${conf.serverName}_warps")

    suspend fun findById(id: UUID): WarpDto? {
        val cached = cache.getIfPresent(id) ?: run {
            val lookup = db.find(eq("id", id.toString())).firstOrNull() ?: return null
            cache.put(id, lookup)
            lookup
        }
        return cached
    }

    suspend fun findByName(name: String): WarpDto? {
        val cached = cache.asMap().values.firstOrNull { it.name == name } ?: run {
            val lookup = db.find(eq("name", name)).firstOrNull() ?: return null
            cache.put(lookup.id, lookup)
            lookup
        }
        return cached
    }

    suspend fun list(): Collection<WarpDto> {
        return mutableListOf<WarpDto>().apply {
            db.find().toCollection(this)
        }
    }

    suspend fun hasById(id: UUID): Boolean {
        return findById(id) != null
    }

    suspend fun hasByName(name: String): Boolean {
        return findByName(name) != null
    }

    suspend fun deleteById(id: UUID) {
        cache.invalidate(id)
        db.deleteOne(eq("id", id.toString()))
    }

    suspend fun deleteByName(name: String) {
        cache.asMap().entries.removeIf { it.value.name == name }
        db.deleteOne(eq("name", name))
    }

    suspend fun save(dto: WarpDto) {
        require(!hasById(dto.id)) { "WarpDto with id ${dto.id} already existed" }
        db.insertOne(dto)
        cache.put(dto.id, dto)
    }

    suspend fun update(dto: WarpDto) {
        require(hasById(dto.id)) { "WarpDto with id ${dto.id} not exist" }
        db.replaceOne(eq("id", dto.id.toString()), dto)
    }
}