package ink.pmc.common.member.data

import ink.pmc.common.member.AbstractMember
import ink.pmc.common.member.api.data.MemberModifier

abstract class AbstractMemberModifier : MemberModifier {

    abstract val member: AbstractMember

}