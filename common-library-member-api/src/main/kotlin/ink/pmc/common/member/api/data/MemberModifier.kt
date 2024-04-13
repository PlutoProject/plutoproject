package ink.pmc.common.member.api.data

import java.time.Instant

@Suppress("UNUSED")
interface MemberModifier {

    fun name(new: String)

    fun createdAt(new: Instant)

    fun lastJoinedAt(new: Instant)

    fun lastQuitedAt(new: Instant)

    fun bio(new: String)

}