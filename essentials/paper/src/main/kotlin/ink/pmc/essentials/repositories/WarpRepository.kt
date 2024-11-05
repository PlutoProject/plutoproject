package ink.pmc.essentials.repositories

import com.github.benmanes.caffeine.cache.Caffeine
import com.mongodb.client.model.Filters.eq
import ink.pmc.essentials.api.warp.WarpCategory
import ink.pmc.essentials.api.warp.WarpType
import ink.pmc.essentials.config.EssentialsConfig
import ink.pmc.essentials.models.WarpModel
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
        .build<UUID, WarpModel>()
    private val db =
        Provider.defaultMongoDatabase.getCollection<WarpModel>("essentials_${conf.serverName}_warps")

    suspend fun find(): Collection<WarpModel> {
        return db.find().toCollection(mutableListOf())
    }

    suspend fun findById(id: UUID): WarpModel? {
        val cached = cache.getIfPresent(id) ?: run {
            val lookup = db.find(eq("id", id.toString())).firstOrNull() ?: return null
            cache.put(id, lookup)
            lookup
        }
        return cached
    }

    suspend fun findByName(name: String): WarpModel? {
        val cached = cache.asMap().values.firstOrNull { it.name == name } ?: run {
            val lookup = db.find(eq("name", name)).firstOrNull() ?: return null
            cache.put(lookup.id, lookup)
            lookup
        }
        return cached
    }

    suspend fun findSpawns(): Collection<WarpModel> {
        return db.find(eq("type", WarpType.SPAWN)).toCollection(mutableListOf())
    }

    suspend fun findByCategory(category: WarpCategory): Collection<WarpModel> {
        return db.find(eq("category", category)).toCollection(mutableListOf())
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

    suspend fun save(model: WarpModel) {
        require(!hasById(model.id)) { "WarpModel with id ${model.id} already existed" }
        db.insertOne(model)
        cache.put(model.id, model)
    }

    suspend fun update(model: WarpModel) {
        require(hasById(model.id)) { "WarpModel with id ${model.id} not exist" }
        db.replaceOne(eq("id", model.id.toString()), model)
    }
}