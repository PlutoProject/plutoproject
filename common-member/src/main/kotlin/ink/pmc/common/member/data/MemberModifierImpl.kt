package ink.pmc.common.member.data

import ink.pmc.common.member.AbstractMember
import java.time.Instant

class MemberModifierImpl(override val member: AbstractMember) : AbstractMemberModifier() {

    override fun name(new: String) {
        member.storage.name = new
    }

    override fun createdAt(new: Instant) {
        member.storage.createdAt = new.toEpochMilli()
    }

    override fun lastJoinedAt(new: Instant) {
        member.storage.lastJoinedAt = new.toEpochMilli()
    }

    override fun bio(new: String) {
        member.storage.bio = new
    }

}