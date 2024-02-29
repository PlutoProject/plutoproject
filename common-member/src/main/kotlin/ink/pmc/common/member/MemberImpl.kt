package ink.pmc.common.member

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import ink.pmc.common.member.api.Member
import ink.pmc.common.member.api.MemberAPI
import ink.pmc.common.member.api.comment.Comment
import ink.pmc.common.member.api.dsl.PunishmentOptionsDSL
import ink.pmc.common.member.api.punishment.PardonReason
import ink.pmc.common.member.api.punishment.Punishment
import ink.pmc.common.member.api.punishment.PunishmentOptions
import ink.pmc.common.member.api.punishment.PunishmentType
import ink.pmc.common.member.comment.CommentDataImpl
import ink.pmc.common.member.comment.CommentImpl
import ink.pmc.common.member.punishment.PunishmentDataImpl
import ink.pmc.common.member.punishment.PunishmentImpl
import org.bson.Document
import org.mongojack.ObjectId
import java.util.*

@Suppress("UNUSED")
data class MemberImpl @JsonCreator constructor(
    @JsonProperty("uuid") override val uuid: UUID,
    @JsonProperty("name") override var name: String,
    @JsonProperty("joinTime") override var joinTime: Date,
    @JsonProperty("lastJoinTime") override var lastJoinTime: Date? = null,
    @JsonProperty("lastQuitTime") override var lastQuitTime: Date? = null,
    @JsonProperty("data") override val data: MutableMap<Any, Any> = mutableMapOf(),
    @JsonProperty("punishmentData") override val punishmentData: PunishmentDataImpl = PunishmentDataImpl(),
    @JsonProperty("commentData") override val commentData: CommentDataImpl = CommentDataImpl(),
    @JsonProperty("bio") override var bio: String? = null
) : Member {

    @ObjectId
    @JsonProperty("_id")
    var id: String? = null

    init {
        joinTime = Date()
        name = name.lowercase()
    }

    override fun punish(options: PunishmentOptions): Punishment {
        val collection = MemberAPIImpl.memberManager.punishmentIdMemberIndexCollection
        val document = collection.find(Filters.exists("lastId")).first()
        val newId = document!!.getLong("lastId") + 1

        val punishment = PunishmentImpl(newId, uuid, options.type)

        punishmentData.punishments.add(punishment)

        if (options.type != PunishmentType.WARN_A && options.type != PunishmentType.WARN_B) {
            punishmentData.currentPunishment = newId
        }

        punishmentData.lastPunishment = newId

        collection.insertOne(Document(mapOf<String, Any>("id" to newId, "owner" to this.uuid)))
        collection.updateOne(Filters.exists("lastId"), Updates.inc("lastId", 1))

        return punishment
    }

    override fun punish(block: PunishmentOptionsDSL.() -> Unit): Punishment {
        val dsl = PunishmentOptionsDSL()
        dsl.block()

        if (dsl.type == null) {
            throw RuntimeException("Required information missed")
        }

        val options = PunishmentOptions(dsl.type!!)
        options.reason = dsl.reason

        return punish(options)
    }

    override fun pardon(reason: PardonReason): Boolean {
        if (punishmentData.currentPunishment == null) {
            return false
        }

        punishmentData.currentPunishmentObj()!!.isPardoned = true
        punishmentData.currentPunishmentObj()!!.pardonReason = reason

        return true
    }

    override fun getPunishment(id: Long): Punishment? {
        if (!punishmentData.punishments.any { it.id == id }) {
            return null
        }

        return punishmentData.punishments.find { it.id == id }
    }

    @Suppress("UNCHECKED_CAST")
    override fun addComment(creator: UUID, content: String): Comment {
        val collection = MemberAPIImpl.memberManager.commentIdMemberIndexCollection

        val emptyIds = collection.find(Filters.exists("emptyIds")).first()?.get(
            "emptyIds"
        ) as? MutableList<Long>

        val newId = if (emptyIds!!.isEmpty()) {
            collection.find(Filters.exists("lastId")).first()?.getLong("lastId")?.plus(1)
        } else {
            emptyIds.first()
        }

        if (emptyIds.isNotEmpty()) {
            emptyIds.removeFirst()
            collection.updateOne(Filters.exists("emptyIds"), Updates.set("emptyIds", emptyIds))
        }

        val comment = CommentImpl(newId!!, uuid, creator, content)
        commentData.comments.add(comment)

        collection.insertOne(Document(mapOf<String, Any>("id" to newId, "owner" to this.uuid)))

        if (emptyIds.isEmpty()) {
            collection.updateOne(Filters.eq("lastId", newId - 1), Updates.inc("lastId", 1))
        }

        return comment
    }

    @Suppress("UNCHECKED_CAST")
    override fun removeComment(id: Long): Boolean {
        commentData.comments.removeIf {
            it.id == id
        }

        val collection = MemberAPIImpl.memberManager.commentIdMemberIndexCollection
        val emptyIds = collection.find(Filters.exists("emptyIds")).first()?.get(
            "emptyIds"
        ) as? MutableList<Long>

        emptyIds?.add(id)

        collection.updateOne(Filters.exists("emptyIds"), Updates.set("emptyIds", emptyIds))
        collection.deleteOne(Filters.eq("id", id))

        return true
    }

    override fun updateComment(id: Long, content: String): Boolean {
        val comment = commentData.comments.first {
            it.id == id
        }

        comment.content = content
        comment.isModified = true

        return true
    }

    override fun getComment(id: Long): Comment? {
        if (!commentData.comments.any { it.id == id }) {
            return null
        }

        return commentData.comments.first { it.id == id }
    }

    override fun update() {
        MemberAPI.instance.memberManager.update(this)
    }

}