package ink.pmc.common.member.api

import com.github.benmanes.caffeine.cache.AsyncLoadingCache
import com.mongodb.client.MongoCollection
import ink.pmc.common.member.api.dsl.MemberDSL
import ink.pmc.common.member.api.punishment.Punishment
import org.bson.Document
import org.mongojack.JacksonMongoCollection
import java.util.*

interface MemberManager {

    val collection: JacksonMongoCollection<Member>
    val punishmentIndexCollection: MongoCollection<Document>
    val commentIndexCollection: MongoCollection<Document>
    val cachedMember: AsyncLoadingCache<UUID, Member>

    suspend fun register(member: Member)

    suspend fun createAndRegister(block: MemberDSL.() -> Unit): Member

    suspend fun update(member: Member): Boolean

    suspend fun get(uuid: UUID): Member?

    suspend fun remove(uuid: UUID): Boolean

    suspend fun exist(uuid: UUID): Boolean

    suspend fun nonExist(uuid: UUID): Boolean

    suspend fun lookupPunishment(id: Long): Punishment?

    suspend fun lookupComment(id: Long): Comment?

    suspend fun sync(member: Member): Boolean

    suspend fun syncAll(): Boolean

}