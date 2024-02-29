package ink.pmc.common.member.api

import com.github.benmanes.caffeine.cache.LoadingCache
import com.mongodb.client.MongoCollection
import ink.pmc.common.member.api.dsl.MemberDSL
import org.bson.Document
import org.mongojack.JacksonMongoCollection
import java.util.*

interface MemberManager {

    val collection: JacksonMongoCollection<out Member>
    val punishmentIdMemberIndexCollection: MongoCollection<Document>
    val commentIdMemberIndexCollection: MongoCollection<Document>
    val cachedMember: LoadingCache<UUID, Member>

    fun register(member: Member)

    fun createAndRegister(block: MemberDSL.() -> Unit): Member

    fun update(member: Member): Boolean

    fun get(uuid: UUID): Member?

    fun exist(uuid: UUID): Boolean

    fun nonExist(uuid: UUID): Boolean

    fun sync(member: Member)

    fun syncAll(): Boolean

}