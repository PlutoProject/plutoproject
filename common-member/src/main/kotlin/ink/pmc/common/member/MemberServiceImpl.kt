package ink.pmc.common.member

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import com.mongodb.client.model.Filters.*
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import ink.pmc.common.member.api.AuthType
import ink.pmc.common.member.api.BedrockAccount
import ink.pmc.common.member.api.Member
import ink.pmc.common.member.api.WhitelistStatus
import ink.pmc.common.member.storage.*
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.bson.types.ObjectId
import java.time.Duration
import java.time.Instant
import java.util.*

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
    override val cache: LoadingCache<String, Optional<Any>> = Caffeine.newBuilder()
        .refreshAfterWrite(Duration.ofMinutes(5))
        .expireAfterWrite(Duration.ofMinutes(5))
        .build { loadCache(it) }

    override fun lookupStatus(): StatusStorage {
        return cache.get("status").get() as StatusStorage
    }

    private fun loadCache(key: String): Optional<Any> {
        return runBlocking {
            val string = key.lowercase()
            val underlineChar = string.indexOf('_')
            val operation = if (underlineChar != -1) {
                string.substring(0, underlineChar)
            } else {
                string
            }
            val id = if (underlineChar != -1) {
                string.substring(underlineChar + 1, string.length).toLong()
            } else {
                -1
            }

            when (operation) {
                "status" -> {
                    val statusStorage = status.find(exists("lastMember")).firstOrNull()
                    Optional.ofNullable(statusStorage)
                }

                "member" -> {
                    val memberStorage = members.find(eq("uid", id)).firstOrNull()
                    Optional.ofNullable(memberStorage)
                }

                "punishment" -> {
                    val punishmentStorage = punishments.find(eq("id", id)).firstOrNull()
                    Optional.ofNullable(punishmentStorage)
                }

                "comment" -> {
                    val commentStorage = comments.find(eq("id", id)).firstOrNull()
                    Optional.ofNullable(commentStorage)
                }

                "dataContainer" -> {
                    val dataContainerStorage = dataContainers.find(eq("id", id)).firstOrNull()
                    Optional.ofNullable(dataContainerStorage)
                }

                "bedrockAccount" -> {
                    val bedrockAccount = dataContainers.find(eq("id", id)).firstOrNull()
                    Optional.ofNullable(bedrockAccount)
                }

                else -> {
                    Optional.empty()
                }
            }
        }
    }

    override fun lookupMember(uid: Long): MemberStorage? {
        val optional = cache.get("member_$uid")

        if (optional.isEmpty) {
            return null
        }

        return optional.get() as MemberStorage
    }

    override fun lookupPunishment(id: Long): PunishmentStorage? {
        val optional = cache.get("punishment_$id")

        if (optional.isEmpty) {
            return null
        }

        return optional.get() as PunishmentStorage
    }

    override fun lookupComment(id: Long): CommentStorage? {
        val optional = cache.get("comment_$id")

        if (optional.isEmpty) {
            return null
        }

        return optional.get() as CommentStorage
    }

    override fun lookupDataContainer(id: Long): DataContainerStorage? {
        val optional = cache.get("dataContainer_$id")

        if (optional.isEmpty) {
            return null
        }

        return optional.get() as DataContainerStorage
    }

    override fun lookupBedrockAccount(id: Long): BedrockAccountStorage? {
        val optional = cache.get("bedrockAccount_$id")

        if (optional.isEmpty) {
            return null
        }

        return optional.get() as BedrockAccountStorage
    }

    override fun cacheStatus(status: StatusStorage) {
        cache.put("status", Optional.of(status))
    }

    override fun cacheMember(uid: Long, member: MemberStorage) {
        cache.put("member_$uid", Optional.of(member))
    }

    override fun cachePunishment(id: Long, punishment: PunishmentStorage) {
        cache.put("punishment_$id", Optional.of(punishment))
    }

    override fun cacheComment(id: Long, comment: CommentStorage) {
        cache.put("comment_$id", Optional.of(comment))
    }

    override fun cacheDataContainer(id: Long, dataContainer: DataContainerStorage) {
        cache.put("dataContainer_$id", Optional.of(dataContainer))
    }

    override fun cacheBedrockAccount(id: Long, bedrockAccount: BedrockAccount) {
        cache.put("bedrockAccount_$id", Optional.of(bedrockAccount))
    }

    override fun clearMember(uid: Long) {
        cache.invalidate("member_$uid")
    }

    override fun clearPunishment(id: Long) {
        cache.invalidate("punishment_$id")
    }

    override fun clearComment(id: Long) {
        cache.invalidate("comment_$id")
    }

    override fun clearDataContainer(id: Long) {
        cache.invalidate("dataContainer_$id")
    }

    override fun clearBedrockAccount(id: Long) {
        cache.invalidate("bedrockAccount_$id")
    }

    override suspend fun lastUid(): Long {
        return currentStatus.get().lastMember
    }

    override suspend fun lastMember(): Member? {
        return lookup(currentStatus.get().lastMember)
    }

    override suspend fun lastMemberCreatedAt(): Instant? {
        return lastMember()?.createdAt
    }

    init {
        runBlocking {
            val currentStatus = status.find(exists("lastMember")).firstOrNull()

            if (currentStatus == null) {
                val initStatusStorage = StatusStorage(ObjectId(), -1, -1, -1, -1, -1)
                cacheStatus(initStatusStorage)
                status.insertOne(initStatusStorage)
                return@runBlocking
            }

            cacheStatus(currentStatus)
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
            return MemberImpl(this, memberStorage)
        }

        val id = authType.fetcher.fetch(name) ?: return null
        val nextMember = currentStatus.get().nextMember()
        val nextDataContainer = currentStatus.get().nextDataContainer()

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
            null,
            mutableListOf(),
            mutableListOf()
        )
        val dataContainerStorage = DataContainerStorage(
            ObjectId(),
            nextDataContainer,
            nextMember,
            System.currentTimeMillis(),
            System.currentTimeMillis(),
            mutableMapOf()
        )

        cacheMember(nextMember, memberStorage)
        cacheDataContainer(nextDataContainer, dataContainerStorage)

        members.insertOne(memberStorage)
        dataContainers.insertOne(dataContainerStorage)
        return MemberImpl(this, memberStorage)
    }

    override suspend fun lookup(uid: Long): Member? {
        if (!exist(uid)) {
            return null
        }

        return MemberImpl(this, lookupMember(uid)!!)
    }

    override fun get(uid: Long): Member? = runBlocking { lookup(uid) }

    override suspend fun exist(uid: Long): Boolean = lookupMember(uid) != null

    override suspend fun existPunishment(id: Long): Boolean = lookupPunishment(id) != null

    override suspend fun existComment(id: Long): Boolean = lookupComment(id) != null

    override suspend fun existDataContainer(id: Long): Boolean = lookupDataContainer(id) != null

    override suspend fun existBedrockAccount(id: Long): Boolean = lookupBedrockAccount(id) != null

    override suspend fun update(member: Member) {
        TODO("Not yet implemented")
    }

    override suspend fun refresh(member: Member) {
        TODO("Not yet implemented")
    }

}