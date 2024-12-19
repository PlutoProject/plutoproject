package ink.pmc.framework.playerdb

import ink.pmc.framework.player.db.Database
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.BsonDocument

fun Database.toModel(): DatabaseModel {
    return DatabaseModel(id.toString(), contents)
}

@Serializable
data class DatabaseModel(
    @SerialName("_id") val id: String,
    @Contextual val contents: BsonDocument
)