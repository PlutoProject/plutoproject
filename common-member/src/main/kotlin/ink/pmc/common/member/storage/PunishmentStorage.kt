package ink.pmc.common.member.storage

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class PunishmentStorage(
    @BsonId val objectId: ObjectId,
    val id: Long,
    val type: String,
    val time: Long,
    val belongs: Long,
    var isRevoked: Boolean,
    val executor: Long
)