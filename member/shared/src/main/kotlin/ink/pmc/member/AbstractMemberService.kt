package ink.pmc.member

import com.github.benmanes.caffeine.cache.AsyncLoadingCache
import com.mongodb.kotlin.client.coroutine.MongoCollection
import ink.pmc.member.api.IMemberService
import ink.pmc.member.storage.BedrockAccountBean
import ink.pmc.member.storage.DataContainerBean
import ink.pmc.member.storage.MemberBean
import ink.pmc.member.storage.StatusBean
import java.io.Closeable
import java.util.*

abstract class AbstractMemberService : IMemberService, Closeable {

    val id: UUID = UUID.randomUUID()
    abstract val statusCollection: MongoCollection<StatusBean>
    abstract val members: MongoCollection<MemberBean>
    abstract val dataContainers: MongoCollection<DataContainerBean>
    abstract val bedrockAccounts: MongoCollection<BedrockAccountBean>
    abstract val loadedMembers: AsyncLoadingCache<Long, AbstractMember?>
    abstract val currentStatus: StatusBean

    abstract suspend fun lookupDataContainerStorage(id: Long): DataContainerBean?

    abstract suspend fun lookupBedrockAccountStorage(id: Long): BedrockAccountBean?
}