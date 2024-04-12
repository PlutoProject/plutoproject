package ink.pmc.common.refactor.member.api

import ink.pmc.common.refactor.member.api.comment.CommentRepository
import ink.pmc.common.refactor.member.api.data.DataContainer
import ink.pmc.common.refactor.member.api.data.MemberModifier
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
    val comments: CommentRepository
    val modifier: MemberModifier

    fun exemptWhitelist()

    fun grantWhitelist()

    suspend fun linkBedrock(xuid: String, gamertag: String): BedrockAccount?

    suspend fun update()

    suspend fun refresh()

}