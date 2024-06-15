package ink.pmc.member.storage

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.BsonDocument
import org.bson.types.ObjectId

@Serializable
data class DataContainerBean(
    @SerialName("_id") @Contextual val objectId: ObjectId,
    var id: Long,
    var owner: Long,
    var createdAt: Long,
    var lastModifiedAt: Long,
    @Contextual var contents: BsonDocument,
)