package ink.pmc.common.member

import com.github.benmanes.caffeine.cache.CacheLoader
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import ink.pmc.common.member.api.Comment
import ink.pmc.common.member.api.Member
import ink.pmc.common.member.api.MemberAPI
import ink.pmc.common.member.api.MemberManager
import ink.pmc.common.member.api.dsl.MemberDSL
import ink.pmc.common.member.api.punishment.Punishment
import org.bson.Document
import org.mongojack.JacksonMongoCollection
import java.time.Duration
import java.util.*

class MemberManagerImpl(
    override val collection: JacksonMongoCollection<Member>,
    override val punishmentIndexCollection: MongoCollection<Document>,
    override val commentIndexCollection: MongoCollection<Document>
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
        if (punishmentIndexCollection.find(Filters.exists("lastId")).first() == null) {
            val document = Document(mapOf("lastId" to -1L))
            punishmentIndexCollection.insertOne(document)
        }

        if (commentIndexCollection.find(Filters.exists("lastId")).first() == null) {
            val document = Document(mapOf("lastId" to -1L, "emptyIds" to listOf<Long>()))
            commentIndexCollection.insertOne(document)
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

    override fun lookupPunishment(id: Long): Punishment? {
        val document = punishmentIndexCollection.find(Filters.eq("id", id)).first() ?: return null
        val ownerUUID = document["owner"] as UUID
        return get(ownerUUID)!!.getPunishment(id)
    }

    override fun lookupComment(id: Long): Comment? {
        val document = commentIndexCollection.find(Filters.eq("id", id)).first() ?: return null
        val ownerUUID = document["owner"] as UUID
        return get(ownerUUID)!!.getComment(id)
    }

    override fun sync(member: Member): Boolean {
        try {
            cachedMember.refresh(member.uuid)
        } catch (e: Exception) {
            return false
        }

        return true
    }

    override fun syncAll(): Boolean {
        try {
            cachedMember.invalidateAll()
        } catch (e: Exception) {
            return false
        }

        return true
    }

    private fun insertAndUpdateCache(member: Member) {
        collection.insertOne(member)
        cachedMember.put(member.uuid, member)
    }

}