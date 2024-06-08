package ink.pmc.member.data

import ink.pmc.member.AbstractMember
import ink.pmc.member.api.data.MemberModifier

abstract class AbstractMemberModifier : MemberModifier {

    abstract val member: AbstractMember

}