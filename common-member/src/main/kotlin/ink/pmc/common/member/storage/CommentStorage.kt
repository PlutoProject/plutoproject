package ink.pmc.common.member.storage

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonIgnore
import org.bson.types.ObjectId

data class CommentStorage(
    @BsonId val objectId: ObjectId,
    val id: Long,
    val createdAt: Long,
    val creator: Long,
    var content: String,
    var isModified: Boolean
)