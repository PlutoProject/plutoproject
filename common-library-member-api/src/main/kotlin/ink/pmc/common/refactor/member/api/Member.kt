package ink.pmc.common.refactor.member.api

import ink.pmc.common.refactor.member.api.comment.Comment
import ink.pmc.common.refactor.member.api.comment.CommentHistory
import ink.pmc.common.refactor.member.api.data.BioHistory
import ink.pmc.common.refactor.member.api.data.DataContainer
import ink.pmc.common.refactor.member.api.dsl.CommentDsl
import ink.pmc.common.refactor.member.api.dsl.PardonDsl
import ink.pmc.common.refactor.member.api.dsl.PunishmentDsl
import ink.pmc.common.refactor.member.api.punishment.PunishmentHistory
import ink.pmc.common.refactor.member.api.punishment.PunishmentLog
import java.time.LocalDateTime
import java.util.*

@Suppress("UNUSED")
const val UID_START = 10000

@Suppress("UNUSED")
interface Member {

    val uid: Long
    val id: UUID
    val name: String
    val whitelistStatus: WhitelistStatus
    val authType: AuthType
    val createdAt: LocalDateTime
    val lastJoinedAt: LocalDateTime?
    val dataContainer: DataContainer
    val bedrockAccount: BedrockAccount?
    val punishmentHistory: PunishmentHistory
    val commentHistory: CommentHistory
    val bioHistory: BioHistory

    fun exemptWhitelist()

    fun grantWhitelist()

    fun punish(block: PunishmentDsl.() -> Unit): PunishmentLog

    fun hasPunishment(id: Long): Boolean

    fun pardon(block: PardonDsl.() -> Unit): PunishmentLog

    fun comment(block: CommentDsl.() -> Unit): Comment

    fun update()

    fun refresh()

}