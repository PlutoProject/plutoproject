package ink.pmc.common.refactor.member.storage

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class DataContainerStorage(
    @BsonId val objectId: ObjectId,
    val id: Long,
    val owner: Long,
    val createdAt: Long,
    val lastModifiedAt: Long,
    val contents: MutableMap<String, Any>
)