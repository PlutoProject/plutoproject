package ink.pmc.common.member.api

import ink.pmc.common.member.api.dsl.PunishmentOptionsDSL
import ink.pmc.common.member.api.punishment.PardonReason
import ink.pmc.common.member.api.punishment.Punishment
import ink.pmc.common.member.api.punishment.PunishmentOptions
import java.util.*


interface Member {

    val uuid: UUID
    var name: String
    var joinTime: Date
    var lastJoinTime: Date?
    var lastQuitTime: Date?
    val punishments: MutableCollection<Punishment>
    var currentPunishment: Long?
    var lastPunishment: Long?
    val comments: MutableCollection<Comment>
    val data: MemberData
    var bio: String?

    fun punish(options: PunishmentOptions): Punishment?

    fun punish(block: PunishmentOptionsDSL.() -> Unit): Punishment?

    fun pardon(reason: PardonReason): Boolean

    fun getPunishment(id: Long): Punishment?

    fun currentPunishmentInstance(): Punishment? =
        if (!punishments.any { it.id == currentPunishment }) null else punishments.first { it.id == currentPunishment }

    fun lastPunishmentInstance(): Punishment? =
        if (!punishments.any { it.id == lastPunishment }) null else punishments.first { it.id == lastPunishment }

    fun createComment(creator: UUID, content: String): Comment

    fun removeComment(id: Long): Boolean

    fun updateComment(id: Long, content: String): Boolean

    fun getComment(id: Long): Comment?

    fun update()

}