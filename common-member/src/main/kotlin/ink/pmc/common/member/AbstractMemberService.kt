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

    abstract fun cachedStatus(): StatusStorage

    abstract fun cachedMember(uid: Long): MemberStorage?

    abstract fun cachedPunishment(id: Long): PunishmentStorage?

    abstract fun cachedComment(id: Long): CommentStorage?

    abstract fun cachedDataContainer(id: Long): DataContainerStorage?

    abstract fun cachedBedrockAccount(id: Long): BedrockAccountStorage?

    abstract fun cacheStatus(status: StatusStorage)

    abstract fun cacheMember(uid: Long, member: MemberStorage)

    abstract fun cachePunishment(id: Long, punishment: PunishmentStorage)

    abstract fun cacheComment(id: Long, comment: CommentStorage)

    abstract fun cacheDataContainer(id: Long, dataContainer: DataContainerStorage)

    abstract fun cacheBedrockAccount(id: Long, bedrockAccount: BedrockAccount)

    abstract fun clearMemberCache(uid: Long)

    abstract fun clearPunishmentCache(id: Long)

    abstract fun clearCommentCache(id: Long)

    abstract fun clearDataContainerCache(id: Long)

    abstract fun clearBedrockAccountCache(id: Long)

    abstract fun isMemberCached(uid: Long): Boolean

    abstract fun isPunishmentCached(id: Long): Boolean

    abstract fun isCommentCached(id: Long): Boolean

    abstract fun isDataContainerCached(id: Long): Boolean

    abstract fun isBedrockAccountCached(id: Long): Boolean

}