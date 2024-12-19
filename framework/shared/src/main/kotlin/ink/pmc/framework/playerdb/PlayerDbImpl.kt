package ink.pmc.framework.playerdb

import com.github.benmanes.caffeine.cache.Caffeine
import ink.pmc.framework.concurrent.submitAsync
import ink.pmc.framework.player.db.Database
import ink.pmc.framework.player.db.PlayerDb
import kotlinx.coroutines.future.asCompletableFuture
import kotlinx.coroutines.future.await
import org.bson.BsonDocument
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration

class PlayerDbImpl : PlayerDb, KoinComponent {

    private val repo by inject<DatabaseRepository>()
    private val loadedDatabases = Caffeine.newBuilder()
        .expireAfterAccess(20.minutes.toJavaDuration())
        .refreshAfterWrite(5.minutes.toJavaDuration())
        .buildAsync<UUID, Database?> { k, _ ->
            submitAsync<Database?> {
                repo.findById(k)?.let { DatabaseImpl(it) }
            }.asCompletableFuture()
        }

    override fun isLoaded(id: UUID): Boolean {
        return getIfLoaded(id) != null
    }

    override fun getIfLoaded(id: UUID): Database? {
        return loadedDatabases.getIfPresent(id)?.get()
    }

    override fun unload(id: UUID): Database? {
        val keep = getIfLoaded(id) ?: return null
        loadedDatabases.synchronous().invalidate(id)
        return keep
    }

    override fun unloadAll() {
        loadedDatabases.synchronous().invalidateAll()
    }

    override suspend fun reload(id: UUID): Database? {
        return loadedDatabases.synchronous().refresh(id).await()
    }

    override suspend fun get(id: UUID): Database? {
        return loadedDatabases.get(id).await()
    }

    override suspend fun getOrCreate(id: UUID): Database {
        return get(id) ?: create(id)
    }

    override suspend fun has(id: UUID): Boolean {
        return repo.hasById(id)
    }

    override suspend fun create(id: UUID): Database {
        return DatabaseImpl(DatabaseModel(id.toString(), BsonDocument())).also {
            it.update()
            loadedDatabases.put(id, CompletableFuture.completedFuture(it))
        }
    }

    override suspend fun delete(id: UUID) {
        repo.deleteById(id)
    }

}