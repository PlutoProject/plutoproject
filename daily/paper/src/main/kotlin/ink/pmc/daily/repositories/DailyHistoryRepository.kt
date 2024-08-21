package ink.pmc.daily.repositories

import com.mongodb.client.model.Filters.*
import com.mongodb.client.model.ReplaceOptions
import com.mongodb.kotlin.client.coroutine.MongoCollection
import ink.pmc.daily.models.DailyHistoryModel
import kotlinx.coroutines.flow.firstOrNull
import java.time.Instant
import java.util.*

class DailyHistoryRepository(private val collection: MongoCollection<DailyHistoryModel>) {

    private val upsert = ReplaceOptions().upsert(true)

    suspend fun findById(id: UUID): DailyHistoryModel? {
        return collection.find(eq("_id", id.toString())).firstOrNull()
    }

    suspend fun findByTime(start: Instant, end: Instant): DailyHistoryModel? {
        return findByTime(start.toEpochMilli(), end.toEpochMilli())
    }

    suspend fun findByTime(start: Long, end: Long): DailyHistoryModel? {
        return collection.find(and(gte("createdAt", start), lte("createdAt", end))).firstOrNull()
    }

    suspend fun deleteById(id: UUID) {
        collection.deleteOne(eq("_id", id.toString()))
    }

    suspend fun deleteByTime(start: Instant, end: Instant) {
        return deleteByTime(start.toEpochMilli(), end.toEpochMilli())
    }

    suspend fun deleteByTime(start: Long, end: Long) {
        collection.deleteOne(and(gte("createdAt", start), lte("createdAt", end)))
    }

    suspend fun saveOrUpdate(model: DailyHistoryModel) {
        collection.replaceOne(eq("_id", model.id), model, upsert)
    }

}