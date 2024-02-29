package ink.pmc.common.member.api

import ink.pmc.common.member.api.comment.Comment
import ink.pmc.common.member.api.comment.CommentData
import ink.pmc.common.member.api.dsl.PunishmentOptionsDSL
import ink.pmc.common.member.api.punishment.PardonReason
import ink.pmc.common.member.api.punishment.Punishment
import ink.pmc.common.member.api.punishment.PunishmentData
import ink.pmc.common.member.api.punishment.PunishmentOptions
import java.util.*


interface Member {

    val uuid: UUID
    var name: String
    var joinTime: Date
    var lastJoinTime: Date?
    var lastQuitTime: Date?
    val data: MutableMap<Any, Any>
    val punishmentData: PunishmentData
    val commentData: CommentData
    var bio: String?

    fun punish(options: PunishmentOptions): Punishment?

    fun punish(block: PunishmentOptionsDSL.() -> Unit): Punishment?

    fun pardon(reason: PardonReason): Boolean

    fun getPunishment(id: Long): Punishment?

    fun addComment(creator: UUID, content: String): Comment

    fun removeComment(id: Long): Boolean

    fun updateComment(id: Long, content: String): Boolean

    fun getComment(id: Long): Comment?

    fun update()

}