package ink.pmc.daily.repositories

import com.github.benmanes.caffeine.cache.Caffeine
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.ReplaceOptions
import com.mongodb.kotlin.client.coroutine.MongoCollection
import ink.pmc.daily.models.HistoryModel
import ink.pmc.utils.concurrent.submitAsync
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.future.asCompletableFuture
import kotlinx.coroutines.future.await
import org.bukkit.OfflinePlayer
import java.util.*
import java.util.concurrent.TimeUnit

class HistoryRepository(private val collection: MongoCollection<HistoryModel>) {

    private val cache = Caffeine.newBuilder()
        .maximumSize(50)
        .expireAfterAccess(10, TimeUnit.MINUTES)
        .buildAsync<UUID, HistoryModel?> { uuid, _ ->
            submitAsync<HistoryModel?> {
                collection.find(eq("_id", uuid.toString())).firstOrNull()
            }.asCompletableFuture()
        }

    private val upsert = ReplaceOptions().upsert(true)

    suspend fun findById(id: UUID): HistoryModel? {
        return cache.get(id).await()
    }

    suspend fun findByOwner(owner: OfflinePlayer): List<HistoryModel> {
        return collection.find(eq("owner", owner.uniqueId.toString())).toList()
    }

    suspend fun existsById(id: UUID): Boolean {
        return findById(id) != null
    }

    suspend fun saveOrUpdate(model: HistoryModel) {
        collection.replaceOne(eq("_id", model.id), model, upsert)
    }

}