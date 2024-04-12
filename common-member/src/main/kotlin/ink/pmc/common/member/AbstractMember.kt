package ink.pmc.common.member

import ink.pmc.common.member.api.Member
import ink.pmc.common.member.storage.MemberStorage

abstract class AbstractMember : Member {

    abstract val storage: MemberStorage

}