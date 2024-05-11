package ink.pmc.common.member.api

import ink.pmc.common.member.api.comment.CommentContainer
import ink.pmc.common.member.api.data.DataContainer
import ink.pmc.common.member.api.data.MemberModifier
import ink.pmc.common.member.api.punishment.PunishmentContainer
import java.time.Instant
import java.util.*

@Suppress("UNUSED")
interface Member {

    val uid: Long
    val id: UUID
    val name: String
    val rawName: String
    val whitelistStatus: WhitelistStatus
    val isWhitelisted: Boolean
    val authType: AuthType
    val createdAt: Instant
    val lastJoinedAt: Instant?
    val lastQuitedAt: Instant?
    val dataContainer: DataContainer
    val bedrockAccount: BedrockAccount?
    val bio: String?
    val isHidden: Boolean
    val commentContainer: CommentContainer
    val punishmentContainer: PunishmentContainer
    val modifier: MemberModifier

    fun exemptWhitelist()

    fun grantWhitelist()

    suspend fun linkBedrock(xuid: String, gamertag: String): BedrockAccount?

    fun unlinkBedrock()

    suspend fun isValid(): Boolean

    suspend fun save()

    suspend fun reload(): Member?

}