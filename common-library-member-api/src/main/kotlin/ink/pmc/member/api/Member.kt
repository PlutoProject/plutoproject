package ink.pmc.member.api

import ink.pmc.member.api.data.DataContainer
import ink.pmc.member.api.data.MemberModifier
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
    val modifier: MemberModifier

    fun exemptWhitelist()

    fun grantWhitelist()

    suspend fun linkBedrock(xuid: String, gamertag: String): BedrockAccount?

    fun unlinkBedrock()

    suspend fun isValid(): Boolean

    suspend fun save()

    suspend fun sync(): Member?

}