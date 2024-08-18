package ink.pmc.daily.repositories

import com.github.benmanes.caffeine.cache.Caffeine
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.ReplaceOptions
import com.mongodb.kotlin.client.coroutine.MongoCollection
import ink.pmc.daily.models.PlayerModel
import ink.pmc.utils.concurrent.submitAsync
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.future.asCompletableFuture
import kotlinx.coroutines.future.await
import java.util.*
import java.util.concurrent.TimeUnit

class PlayerRepository(private val collection: MongoCollection<PlayerModel>) {

    private val cache = Caffeine.newBuilder()
        .maximumSize(50)
        .expireAfterAccess(10, TimeUnit.MINUTES)
        .buildAsync<UUID, PlayerModel?> { uuid, _ ->
            submitAsync<PlayerModel?> {
                collection.find(eq("_id", uuid.toString())).firstOrNull()
            }.asCompletableFuture()
        }

    private val upsert = ReplaceOptions().upsert(true)

    suspend fun findById(id: UUID): PlayerModel? {
        return cache.get(id).await()
    }

    suspend fun existsById(id: UUID): Boolean {
        return findById(id) != null
    }

    suspend fun saveOrUpdate(model: PlayerModel) {
        collection.replaceOne(eq("_id", model.id), model, upsert)
    }

}