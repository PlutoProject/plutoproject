package ink.pmc.options.repositories

import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.ReplaceOptions
import com.mongodb.kotlin.client.coroutine.MongoCollection
import ink.pmc.options.models.OptionsContainerModel
import kotlinx.coroutines.flow.firstOrNull
import java.util.UUID

class OptionsContainerRepository(private val collection: MongoCollection<OptionsContainerModel>) {
    private val upsert = ReplaceOptions().upsert(true)

    suspend fun findById(uuid: UUID): OptionsContainerModel? {
        return collection.find(eq("_id", uuid.toString())).firstOrNull()
    }

    suspend fun deleteById(uuid: UUID) {
        collection.deleteOne(eq("_id", uuid.toString()))
    }

    suspend fun saveOrUpdate(model: OptionsContainerModel) {
        collection.replaceOne(eq("_id", model.id), model, upsert)
    }
}