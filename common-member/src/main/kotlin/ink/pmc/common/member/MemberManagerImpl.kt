package ink.pmc.common.member

import com.github.benmanes.caffeine.cache.CacheLoader
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import ink.pmc.common.member.api.Member
import ink.pmc.common.member.api.MemberAPI
import ink.pmc.common.member.api.MemberManager
import ink.pmc.common.member.api.dsl.MemberDSL
import org.bson.Document
import org.mongojack.JacksonMongoCollection
import java.time.Duration
import java.util.*

class MemberManagerImpl(
    override val collection: JacksonMongoCollection<Member>,
    override val punishmentIdMemberIndexCollection: MongoCollection<Document>,
    override val commentIdMemberIndexCollection: MongoCollection<Document>
) : MemberManager {

    private val cacheLoader: CacheLoader<UUID, Member> = CacheLoader<UUID, Member> {
        collection.find(Filters.eq("uuid", it)).first()
    }

    override val cachedMember: LoadingCache<UUID, Member> = Caffeine.newBuilder()
        .maximumSize(10000)
        .expireAfterWrite(Duration.ofMinutes(30))
        .refreshAfterWrite(Duration.ofMinutes(5))
        .build(cacheLoader)

    init {
        if (punishmentIdMemberIndexCollection.find(Filters.exists("lastId")).first() == null) {
            val document = Document(mapOf("lastId" to -1L))
            punishmentIdMemberIndexCollection.insertOne(document)
        }

        if (commentIdMemberIndexCollection.find(Filters.exists("lastId")).first() == null) {
            val document = Document(mapOf("lastId" to -1L, "emptyIds" to listOf<Long>()))
            commentIdMemberIndexCollection.insertOne(document)
        }
    }

    override fun register(member: Member) {
        if (exist(member.uuid)) {
            return
        }

        insertAndUpdateCache(member)
    }

    override fun createAndRegister(block: MemberDSL.() -> Unit): Member {
        val member = MemberAPI.instance.createMember(block)
        register(member)

        return member
    }

    override fun update(member: Member): Boolean {
        if (nonExist(member.uuid)) {
            return false
        }

        collection.deleteOne(Filters.eq("uuid", member.uuid))
        insertAndUpdateCache(member)

        return true
    }

    override fun get(uuid: UUID): Member? {
        return cachedMember.get(uuid)
    }

    override fun exist(uuid: UUID): Boolean {
        return cachedMember.getIfPresent(uuid) != null || collection.find(Filters.eq("uuid", uuid)).first() != null
    }

    override fun nonExist(uuid: UUID): Boolean {
        return collection.find(Filters.eq("uuid", uuid)).first() == null
    }

    override fun sync(member: Member) {
        cachedMember.refresh(member.uuid)
    }

    override fun syncAll(): Boolean {
        cachedMember.invalidateAll()
        return true
    }

    private fun insertAndUpdateCache(member: Member) {
        collection.insertOne(member)
        cachedMember.put(member.uuid, member)
    }

}