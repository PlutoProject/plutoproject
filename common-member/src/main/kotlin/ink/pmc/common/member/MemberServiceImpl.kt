package ink.pmc.common.member

import com.github.benmanes.caffeine.cache.Caffeine
import com.mongodb.client.model.Filters.*
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import ink.pmc.common.member.api.AuthType
import ink.pmc.common.member.api.Member
import ink.pmc.common.member.api.WhitelistStatus
import ink.pmc.common.member.storage.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.bson.types.ObjectId
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime

@Suppress("UNUSED")
const val UID_START = 10000L

@Suppress("UNUSED")
class MemberServiceImpl(
    database: MongoDatabase
) : AbstractMemberService() {

    override val status: MongoCollection<StatusStorage> = database.getCollection("member_status")
    override val members: MongoCollection<MemberStorage> = database.getCollection("member_members")
    override val punishments: MongoCollection<PunishmentStorage> = database.getCollection("member_punishments")
    override val comments: MongoCollection<CommentStorage> = database.getCollection("member_comments")
    override val dataContainers: MongoCollection<DataContainerStorage> =
        database.getCollection("member_data_containers")
    override val bedrockAccounts: MongoCollection<BedrockAccountStorage> =
        database.getCollection("member_bedrock_accounts")

    private val memberCache = Caffeine.newBuilder()
        .maximumSize(20)
        .expireAfterWrite(Duration.ofMinutes(10))
        .build<Long, MemberStorage> { runBlocking { members.find(eq("uid", it)).firstOrNull() } }
    private val punishmentCache = Caffeine.newBuilder()
        .maximumSize(20)
        .expireAfterWrite(Duration.ofMinutes(10))
        .build<Long, PunishmentStorage> { runBlocking { punishments.find(eq("id", it)).firstOrNull() } }
    private val commentCache = Caffeine.newBuilder()
        .maximumSize(20)
        .expireAfterWrite(Duration.ofMinutes(10))
        .build<Long, CommentStorage> { runBlocking { comments.find(eq("id", it)).firstOrNull() } }
    private val dataContainerCache = Caffeine.newBuilder()
        .maximumSize(20)
        .expireAfterWrite(Duration.ofMinutes(10))
        .build<Long, DataContainerStorage> { runBlocking { dataContainers.find(eq("id", it)).firstOrNull() } }
    private val bedrockAccountCache = Caffeine.newBuilder()
        .maximumSize(20)
        .expireAfterWrite(Duration.ofMinutes(10))
        .build<Long, BedrockAccountStorage> { runBlocking { bedrockAccounts.find(eq("id", it)).firstOrNull() } }

    override suspend fun currentStatus(): StatusStorage {
        return status.find(exists("lastMemberUid")).first()
    }

    override suspend fun updateStatus(new: StatusStorage) {
        status.deleteOne(exists("lastMemberUid"))
        status.insertOne(new)
    }

    override fun lookupMember(uid: Long): MemberStorage = memberCache.get(uid)

    override fun lookupPunishment(id: Long): PunishmentStorage? = punishmentCache.get(id)

    override fun lookupComment(id: Long): CommentStorage? = commentCache.get(id)

    override fun lookupDataContainer(id: Long): DataContainerStorage? = dataContainerCache.get(id)

    override fun lookupBedrockAccount(id: Long): BedrockAccountStorage? = bedrockAccountCache.get(id)

    override fun clearMember(uid: Long) = memberCache.invalidate(uid)

    override fun clearPunishment(id: Long) = punishmentCache.invalidate(id)

    override fun clearComment(id: Long) = commentCache.invalidate(id)

    override fun clearDataContainer(id: Long) = dataContainerCache.invalidate(id)

    override fun clearBedrockAccount(id: Long) = bedrockAccountCache.invalidate(id)

    override suspend fun lastUid(): Long {
        return currentStatus().lastMember
    }

    override suspend fun lastMember(): Member? {
        return lookup(currentStatus().lastMember)
    }

    override suspend fun lastMemberCreatedAt(): Instant? {
        return lastMember()?.createdAt
    }

    init {
        runBlocking {
            val currentStatus = status.find(exists("lastMemberUid")).firstOrNull()

            if (currentStatus == null) {
                val initStatusStorage = StatusStorage(ObjectId(), -1, -1, -1, -1, -1)
                status.insertOne(initStatusStorage)
            }
        }
    }

    override suspend fun create(name: String, authType: AuthType): Member? {
        var memberStorage = members.find(
            and(
                eq("name", name),
                eq("authType", authType.toString())
            )
        ).firstOrNull()

        if (memberStorage != null) {
            return MemberImpl(this, memberStorage.uid)
        }

        val id = authType.fetcher.fetch(name) ?: return null
        val nextMember = currentStatus().nextMember()
        val nextDataContainer = currentStatus().nextDataContainer()

        memberStorage = MemberStorage(
            ObjectId(),
            nextMember,
            id.toString(),
            name,
            WhitelistStatus.WHITELISTED.toString(),
            authType.toString(),
            System.currentTimeMillis(),
            null,
            nextDataContainer,
            null,
            null
        )
        val dataContainerStorage = DataContainerStorage(
            ObjectId(),
            nextDataContainer,
            nextMember,
            System.currentTimeMillis(),
            System.currentTimeMillis(),
            mutableMapOf()
        )

        memberCache.put(nextMember, memberStorage)
        dataContainerCache.put(nextDataContainer, dataContainerStorage)

        members.insertOne(memberStorage)
        dataContainers.insertOne(dataContainerStorage)
        return MemberImpl(this, nextMember)
    }

    override suspend fun lookup(uid: Long): Member? {
        if (nonExist(uid)) {
            return null
        }

        return MemberImpl(this, uid)
    }

    override fun get(uid: Long): Member? {
        return runBlocking { lookup(uid) }
    }

    override suspend fun exist(uid: Long): Boolean {
        return members.find(eq("uid", uid)).firstOrNull() != null
    }

    override suspend fun nonExist(uid: Long): Boolean {
        return !exist(uid)
    }

    override suspend fun update(member: Member) {
        TODO("Not yet implemented")
    }

    override suspend fun refresh(member: Member) {
        TODO("Not yet implemented")
    }

}