package ink.pmc.member.storage

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

@Serializable
data class MemberBean(
    @SerialName("_id") @Contextual val objectId: ObjectId,
    var uid: Long,
    var id: String,
    var name: String,
    var rawName: String,
    var whitelistStatus: String,
    var authType: String,
    var createdAt: Long,
    var lastJoinedAt: Long?,
    var lastQuitedAt: Long?,
    var dataContainer: Long,
    var bedrockAccount: Long?,
    var bio: String?,
    var isHidden: Boolean?,
)