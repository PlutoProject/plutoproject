package ink.pmc.common.member.storage

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import org.javers.core.diff.Diff

data class MemberStorage(
    @BsonId val objectId: ObjectId,
    val uid: Long,
    val id: String,
    var name: String,
    var rawName: String,
    var whitelistStatus: String,
    val authType: String,
    var createdAt: Long,
    var lastJoinedAt: Long?,
    var lastQuitedAt: Long?,
    val dataContainer: Long,
    var bedrockAccount: Long?,
    var bio: String?,
    val punishments: MutableList<Long>,
    val comments: MutableList<Long>,
    val isHidden: Boolean?,
    var new: Boolean = false
) : Diffable<MemberStorage>() {

    override fun applyDiff(diff: Diff) {
        TODO("Not yet implemented")
    }

}