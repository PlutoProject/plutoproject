package ink.pmc.common.refactor.member

import ink.pmc.common.refactor.member.api.Member
import ink.pmc.common.refactor.member.storage.MemberStorage

abstract class AbstractMember : Member {

    abstract val storage: MemberStorage

}