package ink.pmc.common.refactor.member.api

import ink.pmc.common.refactor.member.api.data.DataEntry
import java.time.LocalDateTime
import java.util.*

@Suppress("UNUSED")
interface Member {

    val id: UUID
    val name: String
    val authType: AuthType
    val createdAt: LocalDateTime
    var lastJoinedAt: LocalDateTime?
    var customData: DataEntry
    val bedrockAccount: BedrockAccount?

}