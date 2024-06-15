package ink.pmc.member

import com.github.benmanes.caffeine.cache.AsyncCacheLoader
import com.github.benmanes.caffeine.cache.AsyncLoadingCache
import com.github.benmanes.caffeine.cache.Caffeine
import com.mongodb.client.model.Filters.*
import com.mongodb.client.model.ReplaceOptions
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import ink.pmc.member.api.AuthType
import ink.pmc.member.api.Member
import ink.pmc.member.api.WhitelistStatus
import ink.pmc.member.api.data.MemberModifier
import ink.pmc.member.data.BedrockAccountImpl
import ink.pmc.member.data.DataContainerImpl
import ink.pmc.member.proto.MemberUpdateNotifyOuterClass.MemberUpdateNotify
import ink.pmc.member.proto.memberUpdateNotify
import ink.pmc.member.storage.BedrockAccountBean
import ink.pmc.member.storage.DataContainerBean
import ink.pmc.member.storage.MemberBean
import ink.pmc.member.storage.StatusBean
import ink.pmc.utils.bedrock.xuid
import ink.pmc.utils.concurrent.io
import ink.pmc.utils.concurrent.submitAsyncIO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.future.asCompletableFuture
import kotlinx.coroutines.future.await
import kotlinx.coroutines.withContext
import org.bson.BsonDocument
import org.bson.types.ObjectId
import java.time.Duration
import java.time.Instant
import java.util.*
import java.util.concurrent.CompletableFuture

const val UID_START = 10000L

@Suppress("UNUSED")
abstract class BaseMemberServiceImpl(
    database: MongoDatabase
) : AbstractMemberService() {

    private var closed = false
    override val statusCollection: MongoCollection<StatusBean> = database.getCollection("member_status")
    override val members: MongoCollection<MemberBean> = database.getCollection("member_members")
    override val dataContainers: MongoCollection<DataContainerBean> =
        database.getCollection("member_data_containers")
    override val bedrockAccounts: MongoCollection<BedrockAccountBean> =
        database.getCollection("member_bedrock_accounts")
    private val cacheLoader =
        AsyncCacheLoader<Long, AbstractMember?> { key, _ -> submitAsyncIO<AbstractMember?> { loadMember(key) }.asCompletableFuture() }
    override val loadedMembers: AsyncLoadingCache<Long, AbstractMember?> = Caffeine.newBuilder()
        .expireAfterWrite(Duration.ofMinutes(10))
        .refreshAfterWrite(Duration.ofMinutes(5))
        .buildAsync(cacheLoader)
    override lateinit var currentStatus: StatusBean
    private val replaceOptions = ReplaceOptions().upsert(true)
    private val monitorJob: Job

    override suspend fun lookupBedrockAccountStorage(id: Long): BedrockAccountBean? {
        return withContext(Dispatchers.IO) {
            bedrockAccounts.find(eq("id", id)).firstOrNull()
        }
    }

    override suspend fun lookupDataContainerStorage(id: Long): DataContainerBean? {
        return withContext(Dispatchers.IO) {
            dataContainers.find(eq("id", id)).firstOrNull()
        }
    }

    private suspend fun loadMember(uid: Long): AbstractMember? {
        val memberStorage = members.find(eq("uid", uid)).firstOrNull() ?: return null
        return createMemberInstance(memberStorage) as AbstractMember
    }

    private suspend fun createMemberInstance(storage: MemberBean): Member {
        val service = this
        return withContext(Dispatchers.IO) {
            MemberImpl(service, storage).apply {
                dataContainer = DataContainerImpl(this, lookupDataContainerStorage(storage.dataContainer)!!)
                if (storage.bedrockAccount != null) {
                    bedrockAccount = BedrockAccountImpl(this, lookupBedrockAccountStorage(storage.bedrockAccount!!)!!)
                }
            }
        }
    }

    override suspend fun lastUid(): Long {
        return currentStatus.lastMember
    }

    override suspend fun lastMember(): Member? {
        return lookup(currentStatus.lastMember)
    }

    override suspend fun lastMemberCreatedAt(): Instant? {
        return lastMember()?.createdAt
    }

    init {
        submitAsyncIO {
            var lookupStatus: StatusBean?
            lookupStatus = statusCollection.find(exists("lastMember")).firstOrNull()

            if (lookupStatus == null) {
                lookupStatus = StatusBean(ObjectId(), -1, -1, -1)
                statusCollection.insertOne(lookupStatus)
            }

            currentStatus = lookupStatus
        }

        monitorJob = submitAsyncIO { monitorUpdate() }
    }

    override fun close() {
        monitorJob.cancel()
        closed = true
    }

    override suspend fun create(name: String, authType: AuthType): Member? {
        var memberStorage = members.find(
            and(
                eq("name", name),
                eq("authType", authType.toString())
            )
        ).firstOrNull()

        if (memberStorage != null) {
            return loadedMembers.get(memberStorage.uid).await()
        }

        val profile = authType.fetcher.fetch(name) ?: return null
        val nextMember = currentStatus.nextMember()
        val nextDataContainer = currentStatus.nextDataContainer()
        val nextBedrockAccountId = currentStatus.nextBedrockAccount()

        val dataContainerBean = DataContainerBean(
            ObjectId(),
            nextDataContainer,
            nextMember,
            System.currentTimeMillis(),
            System.currentTimeMillis(),
            BsonDocument(),
        )
        memberStorage = MemberBean(
            ObjectId(),
            nextMember,
            profile.uuid.toString(),
            name.lowercase(),
            name,
            WhitelistStatus.WHITELISTED.toString(),
            authType.toString(),
            System.currentTimeMillis(),
            null,
            null,
            nextDataContainer,
            if (authType.isBedrock) nextBedrockAccountId else null,
            null,
            false,
        )

        val member = MemberImpl(this, memberStorage)
        member.dataContainer = DataContainerImpl(member, dataContainerBean)

        loadedMembers.put(nextMember, CompletableFuture.completedFuture(member))

        if (authType.isBedrock) {
            if (bedrockAccounts.find(eq("xuid", profile.uuid)).firstOrNull() != null) {
                return null
            }

            val bedrockStorage = BedrockAccountBean(
                ObjectId(),
                nextBedrockAccountId,
                nextMember,
                profile.uuid.xuid,
                name,
            )

            member.bedrockAccount = BedrockAccountImpl(member, bedrockStorage)
            currentStatus.increaseBedrockAccount()
        }

        currentStatus.increaseDataContainer()
        currentStatus.increaseMember()
        save(member)

        return member
    }

    override suspend fun lookup(uid: Long): Member? = withContext(Dispatchers.IO) {
        if (!exist(uid)) {
            return@withContext null
        }

        loadedMembers.get(uid).await()
    }

    override suspend fun lookup(uuid: UUID): Member? {
        if (!exist(uuid)) {
            return null
        }

        val storage = members.find(eq("id", uuid.toString())).firstOrNull() ?: return null
        return loadedMembers.get(storage.uid).await()
    }

    override suspend fun lookup(name: String, authType: AuthType): Member? {
        val member = members.find(
            and(
                eq("name", name.lowercase()),
                eq("authType", authType.toString())
            )
        ).firstOrNull() ?: return null
        return loadedMembers.get(member.uid).await()
    }

    override suspend fun exist(uid: Long): Boolean {
        val memberStorage = members.find(eq("uid", uid)).firstOrNull()
        return memberStorage != null
    }

    override suspend fun exist(uuid: UUID): Boolean {
        return members.find(eq("id", uuid.toString())).firstOrNull() != null
    }

    override suspend fun exist(name: String, authType: AuthType): Boolean {
        val member = members.find(
            and(
                eq("name", name.lowercase()),
                eq("authType", authType.toString())
            )
        ).firstOrNull()
        return member != null
    }

    override suspend fun existDataContainer(id: Long): Boolean {
        val dataContainerStorage = dataContainers.find(eq("id", id)).firstOrNull()
        return dataContainerStorage != null
    }

    override suspend fun existBedrockAccount(id: Long): Boolean {
        val bedrockAccountStorage = bedrockAccounts.find(eq("id", id)).firstOrNull()
        return bedrockAccountStorage != null
    }

    override suspend fun isWhitelisted(uid: Long): Boolean {
        return exist(uid) && lookup(uid)!!.isWhitelisted
    }

    override suspend fun isWhitelisted(uuid: UUID): Boolean {
        return exist(uuid) && lookup(uuid)!!.isWhitelisted
    }

    override suspend fun modifier(uid: Long, refresh: Boolean): MemberModifier? {
        if (!exist(uid)) {
            return null
        }

        val member = lookup(uid)

        if (refresh) {
            return member!!.modifier
        }

        return member!!.modifier
    }

    override suspend fun modifier(uuid: UUID, refresh: Boolean): MemberModifier? {
        if (!exist(uuid)) {
            return null
        }

        val member = lookup(uuid)

        if (refresh) {
            return member!!.modifier
        }

        return member!!.modifier
    }

    private suspend fun saveMember(bean: MemberBean) {
        members.replaceOne(eq("uid", bean.uid), bean, replaceOptions)
    }

    private suspend fun saveBedrockAccount(bean: BedrockAccountBean?) {
        removalBeAccounts.forEach {
            bedrockAccounts.deleteOne(eq("id", it))
        }

        removalBeAccounts.clear()

        if (bean == null) {
            return
        }

        bedrockAccounts.replaceOne(eq("id", bean.id), bean, replaceOptions)
    }

    private suspend fun saveDataContainer(bean: DataContainerBean) {
        dataContainers.replaceOne(eq("id", bean.id), bean, replaceOptions)
    }

    private suspend fun saveStatus(bean: StatusBean) {
        statusCollection.replaceOne(exists("lastMember"), bean, replaceOptions)
    }

    abstract suspend fun notifyUpdate(notify: MemberUpdateNotify)

    abstract suspend fun monitorUpdate()

    suspend fun handleUpdate(notify: MemberUpdateNotify) {
        withContext(Dispatchers.IO) {
            if (UUID.fromString(notify.serviceId) == id) {
                return@withContext
            }

            val memberId = notify.memberId

            if (loadedMembers.getIfPresent(memberId) == null) {
                return@withContext
            }

            serverLogger.info("Received update notify from server (serviceId=${notify.serviceId}) for UID ${notify.memberId}, processing...")
            sync(memberId)

            serverLogger.info("Notify processed for UID ${notify.memberId}")
        }
    }

    override suspend fun save(member: Member) {
        if (member !is AbstractMember) {
            return
        }

        io {
            saveStatus(currentStatus)
            saveMember(member.createBean())
            saveBedrockAccount(member.bedrockAccount?.createBean())
            saveDataContainer(member.dataContainer.createBean())

            val notify = memberUpdateNotify {
                serviceId = id.toString()
                memberId = member.uid
            }

            notifyUpdate(notify)
        }
    }

    override suspend fun save(uid: Long) {
        loadedMembers.asMap().values.firstOrNull { it.await()?.uid == uid } ?: return
        save(loadedMembers.get(uid).await()!!)
    }

    override suspend fun save(uuid: UUID) {
        val member = loadedMembers.asMap().values.firstOrNull { it.await()?.id == uuid } ?: return
        save(member.await()!!)
    }

    override suspend fun sync(member: Member): Member? {
        if (!exist(member.uid)) {
            return null
        }

        currentStatus = statusCollection.find(exists("lastMember")).firstOrNull()!!
        loadedMembers.synchronous().invalidate(member.uid)
        return lookup(member.uid)
    }

    override suspend fun sync(uid: Long): Member? {
        if (!exist(uid)) {
            return null
        }

        currentStatus = statusCollection.find(exists("lastMember")).firstOrNull()!!
        loadedMembers.synchronous().invalidate(uid)
        return lookup(uid)
    }

    override suspend fun sync(uuid: UUID): Member? {
        if (!exist(uuid)) {
            return null
        }

        val member = lookup(uuid)
        currentStatus = statusCollection.find(exists("lastMember")).firstOrNull()!!
        loadedMembers.synchronous().invalidate(member!!.uid)
        return lookup(uuid)
    }

}