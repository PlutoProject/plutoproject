package ink.pmc.common.member

import com.github.benmanes.caffeine.cache.AsyncLoadingCache
import com.mongodb.kotlin.client.coroutine.MongoCollection
import ink.pmc.common.member.api.IMemberService
import ink.pmc.common.member.storage.BedrockAccountStorage
import ink.pmc.common.member.storage.DataContainerStorage
import ink.pmc.common.member.storage.MemberStorage
import ink.pmc.common.member.storage.StatusStorage
import java.io.Closeable
import java.util.*

abstract class AbstractMemberService : IMemberService, Closeable {

    val id: UUID = UUID.randomUUID()
    abstract val statusCollection: MongoCollection<StatusStorage>
    abstract val members: MongoCollection<MemberStorage>
    abstract val dataContainers: MongoCollection<DataContainerStorage>
    abstract val bedrockAccounts: MongoCollection<BedrockAccountStorage>
    abstract val loadedMembers: AsyncLoadingCache<Long, AbstractMember?>
    abstract val currentStatus: StatusStorage

    abstract suspend fun lookupDataContainerStorage(id: Long): DataContainerStorage?

    abstract suspend fun lookupBedrockAccountStorage(id: Long): BedrockAccountStorage?

}