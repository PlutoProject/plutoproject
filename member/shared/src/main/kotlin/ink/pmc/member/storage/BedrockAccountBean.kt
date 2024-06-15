package ink.pmc.member.storage

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

@Serializable
data class BedrockAccountBean(
    @SerialName("_id") @Contextual var objectId: ObjectId,
    var id: Long,
    var linkedWith: Long,
    var xuid: String,
    var gamertag: String,
)