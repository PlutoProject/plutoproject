package ink.pmc.common.member.storage

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class BedrockAccountStorage(
    @BsonId val objectId: ObjectId,
    val id: Long,
    val linkedWith: Long,
    val xuid: String,
    val gamertag: String
)