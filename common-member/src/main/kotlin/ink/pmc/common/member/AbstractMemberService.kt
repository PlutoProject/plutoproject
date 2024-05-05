package ink.pmc.common.member

import com.github.benmanes.caffeine.cache.AsyncLoadingCache
import com.mongodb.kotlin.client.coroutine.MongoCollection
import ink.pmc.common.member.api.IMemberService
import ink.pmc.common.member.api.Member
import ink.pmc.common.member.storage.*

abstract class AbstractMemberService : IMemberService {

    abstract val status: MongoCollection<StatusStorage>
    abstract val members: MongoCollection<MemberStorage>
    abstract val punishments: MongoCollection<PunishmentStorage>
    abstract val comments: MongoCollection<CommentStorage>
    abstract val dataContainers: MongoCollection<DataContainerStorage>
    abstract val bedrockAccounts: MongoCollection<BedrockAccountStorage>
    abstract val loadedMembers: AsyncLoadingCache<Long, Member?>
    abstract val currentStatus: StatusStorage

    abstract suspend fun lookupMemberStorage(uid: Long): MemberStorage?

    abstract suspend fun lookupPunishmentStorage(id: Long): PunishmentStorage?

    abstract suspend fun lookupCommentStorage(id: Long): CommentStorage?

    abstract suspend fun lookupDataContainerStorage(id: Long): DataContainerStorage?

    abstract suspend fun lookupBedrockAccount(id: Long): BedrockAccountStorage?

}