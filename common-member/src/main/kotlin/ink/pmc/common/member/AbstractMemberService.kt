package ink.pmc.common.member

import com.mongodb.kotlin.client.coroutine.MongoCollection
import ink.pmc.common.member.api.IMemberService
import ink.pmc.common.member.storage.*

abstract class AbstractMemberService : IMemberService {

    abstract val status: MongoCollection<StatusStorage>
    abstract val members: MongoCollection<MemberStorage>
    abstract val punishments: MongoCollection<PunishmentStorage>
    abstract val comments: MongoCollection<CommentStorage>
    abstract val dataContainers: MongoCollection<DataContainerStorage>
    abstract val bedrockAccounts: MongoCollection<BedrockAccountStorage>

    abstract suspend fun currentStatus(): StatusStorage

    abstract suspend fun updateStatus(new: StatusStorage)

    abstract fun lookupMember(uid: Long): MemberStorage

    abstract fun lookupPunishment(id: Long): PunishmentStorage?

    abstract fun lookupComment(id: Long): CommentStorage?

    abstract fun lookupDataContainer(id: Long): DataContainerStorage?

    abstract fun lookupBedrockAccount(id: Long): BedrockAccountStorage?

    abstract fun clearMember(uid: Long)

    abstract fun clearPunishment(id: Long)

    abstract fun clearComment(id: Long)
    
    abstract fun clearDataContainer(id: Long)

    abstract fun clearBedrockAccount(id: Long)

}