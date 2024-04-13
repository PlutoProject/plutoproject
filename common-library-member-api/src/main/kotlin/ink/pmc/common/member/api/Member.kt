package ink.pmc.common.member.api

import ink.pmc.common.member.api.comment.CommentRepository
import ink.pmc.common.member.api.data.DataContainer
import ink.pmc.common.member.api.data.MemberModifier
import ink.pmc.common.member.api.punishment.PunishmentLogger
import java.time.Instant
import java.util.*

@Suppress("UNUSED")
interface Member {

    val uid: Long
    val id: UUID
    val name: String
    val whitelistStatus: WhitelistStatus
    val isWhitelisted: Boolean
    val authType: AuthType
    val createdAt: Instant
    val lastJoinedAt: Instant?
    val lastQuitedAt: Instant?
    val dataContainer: DataContainer
    val bedrockAccount: BedrockAccount?
    val bio: String?
    val commentRepository: CommentRepository
    val punishmentLogger: PunishmentLogger
    val modifier: MemberModifier

    fun exemptWhitelist()

    fun grantWhitelist()

    fun linkBedrock(xuid: String, gamertag: String): BedrockAccount?

    fun unlinkBedrock()

    suspend fun isValid(): Boolean

    suspend fun update()

    suspend fun refresh(): Member?

}