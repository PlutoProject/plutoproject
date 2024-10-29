package ink.pmc.driftbottle.repositories

import ink.pmc.driftbottle.api.BottleState
import com.mongodb.client.model.Aggregates.sample
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.ReplaceOptions
import com.mongodb.kotlin.client.coroutine.MongoCollection
import ink.pmc.driftbottle.models.BottleModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.koin.core.component.KoinComponent
import java.util.*

@Suppress("UNUSED")
class BottleRepository(private val collection: MongoCollection<BottleModel>) : KoinComponent {
    private val upsert = ReplaceOptions().upsert(true)

    suspend fun findById(id: UUID): BottleModel? {
        return collection.find(eq("_id", id.toString())).firstOrNull()
    }

    suspend fun findByCreator(creator: UUID): List<BottleModel> {
        return collection.find(eq("creator", creator.toString())).toList()
    }

    suspend fun random(): BottleModel? {
        val filter = eq("state", BottleState.IN_SEA)
        return collection.aggregate(listOf(filter, sample(1))).firstOrNull()
    }

    suspend fun deleteById(id: UUID) {
        collection.deleteOne(eq("_id", id.toString()))
    }

    suspend fun count(): Long {
        return collection.countDocuments()
    }

    suspend fun count(state: BottleState): Long {
        return collection.countDocuments(eq("state", state))
    }

    suspend fun count(creator: UUID): Long {
        return collection.countDocuments(eq("creator", creator.toString()))
    }

    suspend fun saveOrUpdate(model: BottleModel) {
        collection.replaceOne(eq("_id", model.id), model, upsert)
    }
}