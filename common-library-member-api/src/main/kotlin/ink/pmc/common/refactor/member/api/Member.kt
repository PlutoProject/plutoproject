package ink.pmc.common.refactor.member.api

import ink.pmc.common.refactor.member.api.data.DataEntry
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
    val customData: DataEntry
    val bedrockAccount: BedrockAccount?

    fun exemptWhitelist()

    fun grantWhitelist()

    fun update()

    fun refresh()

}