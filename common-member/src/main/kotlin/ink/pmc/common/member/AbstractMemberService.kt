package ink.pmc.common.member

import com.github.benmanes.caffeine.cache.LoadingCache
import com.mongodb.kotlin.client.coroutine.MongoCollection
import ink.pmc.common.member.api.BedrockAccount
import ink.pmc.common.member.api.IMemberService
import ink.pmc.common.member.storage.*
import java.util.Optional
import java.util.concurrent.atomic.AtomicReference

abstract class AbstractMemberService : IMemberService {

    abstract val status: MongoCollection<StatusStorage>
    abstract val members: MongoCollection<MemberStorage>
    abstract val punishments: MongoCollection<PunishmentStorage>
    abstract val comments: MongoCollection<CommentStorage>
    abstract val dataContainers: MongoCollection<DataContainerStorage>
    abstract val bedrockAccounts: MongoCollection<BedrockAccountStorage>
    abstract val cache: LoadingCache<String, Optional<Any>>
    val currentStatus: AtomicReference<StatusStorage> = AtomicReference()

    abstract fun lookupStatus(): StatusStorage

    abstract fun lookupMember(uid: Long): MemberStorage?

    abstract fun lookupPunishment(id: Long): PunishmentStorage?

    abstract fun lookupComment(id: Long): CommentStorage?

    abstract fun lookupDataContainer(id: Long): DataContainerStorage?

    abstract fun lookupBedrockAccount(id: Long): BedrockAccountStorage?

    abstract fun cacheStatus(status: StatusStorage)

    abstract fun cacheMember(uid: Long, member: MemberStorage)

    abstract fun cachePunishment(id: Long, punishment: PunishmentStorage)

    abstract fun cacheComment(id: Long, comment: CommentStorage)

    abstract fun cacheDataContainer(id: Long, dataContainer: DataContainerStorage)

    abstract fun cacheBedrockAccount(id: Long, bedrockAccount: BedrockAccount)

    abstract fun clearMember(uid: Long)

    abstract fun clearPunishment(id: Long)

    abstract fun clearComment(id: Long)

    abstract fun clearDataContainer(id: Long)

    abstract fun clearBedrockAccount(id: Long)

}