package ink.pmc.common.refactor.member.api.data

import java.time.LocalDateTime

@Suppress("UNUSED")
interface MemberModifier {

    fun name(new: String)

    fun createdAt(new: LocalDateTime)

    fun lastJoinedAt(new: LocalDateTime)

    fun bio(new: String)

}