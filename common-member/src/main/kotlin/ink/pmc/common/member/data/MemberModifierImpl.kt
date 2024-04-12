package ink.pmc.common.member.data

import ink.pmc.common.member.api.Member
import java.time.Instant

class MemberModifierImpl(override val member: Member) : AbstractMemberModifier() {

    override fun name(new: String) {
        TODO("Not yet implemented")
    }

    override fun createdAt(new: Instant) {
        TODO("Not yet implemented")
    }

    override fun lastJoinedAt(new: Instant) {
        TODO("Not yet implemented")
    }

    override fun bio(new: String) {
        TODO("Not yet implemented")
    }

}