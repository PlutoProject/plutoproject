package ink.pmc.framework.options.repositories

import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.ReplaceOptions
import com.mongodb.kotlin.client.coroutine.MongoCollection
import ink.pmc.framework.options.models.PlayerOptionsModel
import kotlinx.coroutines.flow.firstOrNull
import java.util.*

class OptionsContainerRepository(private val collection: MongoCollection<PlayerOptionsModel>) {
    private val upsert = ReplaceOptions().upsert(true)

    suspend fun findById(uuid: UUID): PlayerOptionsModel? {
        return collection.find(eq("_id", uuid.toString())).firstOrNull()
    }

    suspend fun deleteById(uuid: UUID) {
        collection.deleteOne(eq("_id", uuid.toString()))
    }

    suspend fun saveOrUpdate(model: PlayerOptionsModel) {
        collection.replaceOne(eq("_id", model.id), model, upsert)
    }
}