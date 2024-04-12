package ink.pmc.common.refactor.member

import com.mongodb.client.model.Filters.*
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import ink.pmc.common.refactor.member.api.AuthType
import ink.pmc.common.refactor.member.api.Member
import ink.pmc.common.refactor.member.api.WhitelistStatus
import ink.pmc.common.refactor.member.storage.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.bson.types.ObjectId
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

    override suspend fun currentStatus(): StatusStorage {
        return status.find(exists("lastMemberUid")).first()
    }

    override suspend fun updateStatus(new: StatusStorage) {
        status.deleteOne(exists("lastMemberUid"))
        status.insertOne(new)
    }

    override suspend fun lastUid(): Long {
        return currentStatus().lastMember
    }

    override suspend fun lastMember(): Member? {
        return lookup(currentStatus().lastMember)
    }

    override suspend fun lastMemberCreatedAt(): LocalDateTime? {
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
            return MemberImpl(memberStorage)
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

        members.insertOne(memberStorage)
        dataContainers.insertOne(dataContainerStorage)
        return MemberImpl(memberStorage)
    }

    override suspend fun lookup(uid: Long): Member? {
        if (nonExist(uid)) {
            return null
        }

        val memberStorage = members.find(eq("uid", uid)).firstOrNull()!!
        return MemberImpl(memberStorage)
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