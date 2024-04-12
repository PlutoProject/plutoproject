package ink.pmc.common.member.data

import ink.pmc.common.member.api.Member
import ink.pmc.common.member.api.data.MemberModifier

abstract class AbstractMemberModifier : MemberModifier {

    abstract val member: Member

}