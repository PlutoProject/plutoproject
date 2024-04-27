package ink.pmc.common.member.data

import ink.pmc.common.member.AbstractMember
import ink.pmc.common.member.MemberImpl
import java.time.Instant

class MemberModifierImpl(override val member: AbstractMember) : AbstractMemberModifier() {

    private val impl = member as MemberImpl

    override fun name(new: String) {
        impl.name = new
    }

    override fun createdAt(new: Instant) {
        impl.createdAt = new
    }

    override fun lastJoinedAt(new: Instant) {
        impl.lastJoinedAt = new
    }

    override fun lastQuitedAt(new: Instant) {
        impl.lastQuitedAt = new
    }

    override fun bio(new: String) {
        impl.bio = new
    }

    override fun hide(new: Boolean) {
        impl.isHidden = new
    }

}