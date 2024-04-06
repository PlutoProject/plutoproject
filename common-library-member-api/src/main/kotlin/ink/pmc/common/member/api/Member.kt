package ink.pmc.common.member.api

import ink.pmc.common.member.api.dsl.PunishmentOptionsDSL
import ink.pmc.common.member.api.punishment.PardonReason
import ink.pmc.common.member.api.punishment.Punishment
import ink.pmc.common.member.api.punishment.PunishmentOptions
import java.util.*

@Suppress("UNUSED")
interface Member {

    val uuid: UUID
    var name: String
    var joinTime: Long
    var lastJoinTime: Long?
    var lastQuitTime: Long?
    val punishments: MutableCollection<Punishment>
    var currentPunishmentId: Long?
    var lastPunishmentId: Long?
    val comments: MutableCollection<Comment>
    val data: MemberData
    var bio: String?
    var totalPlayTime: Long
    val currentPunishment
        get() = if (!punishments.any { it.id == currentPunishmentId }) null else punishments.first { it.id == currentPunishmentId }
    val lastPunishment
        get() = if (!punishments.any { it.id == lastPunishmentId }) null else punishments.first { it.id == lastPunishmentId }

    suspend fun punish(options: PunishmentOptions): Punishment?

    suspend fun punish(block: PunishmentOptionsDSL.() -> Unit): Punishment?

    fun pardon(reason: PardonReason): Boolean

    fun getPunishment(id: Long): Punishment?

    suspend fun createComment(creator: UUID, content: String): Comment

    suspend fun removeComment(id: Long): Boolean

    fun updateComment(id: Long, content: String): Boolean

    fun getComment(id: Long): Comment?

    fun increasePlayTime(ms: Long)

    suspend fun update()

    suspend fun sync()

}