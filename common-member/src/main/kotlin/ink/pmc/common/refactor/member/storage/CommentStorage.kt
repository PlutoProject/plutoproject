package ink.pmc.common.refactor.member.storage

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class CommentStorage(
    @BsonId val objectId: ObjectId,
    val id: Long,
    val createdAt: Long,
    val creator: Long,
    val content: String,
    val isModified: Boolean
)