package ink.pmc.common.member

import ink.pmc.common.member.api.Member
import ink.pmc.common.member.data.AbstractBedrockAccount
import ink.pmc.common.member.data.AbstractDataContainer
import ink.pmc.common.member.storage.MemberStorage

abstract class AbstractMember : Member {

    abstract val storage: MemberStorage
    abstract override val bedrockAccount: AbstractBedrockAccount?
    abstract override val dataContainer: AbstractDataContainer

    override fun equals(other: Any?): Boolean {
        if (other !is Member) {
            return false
        }

        return other.uid == uid
    }

    override fun hashCode(): Int {
        return storage.hashCode()
    }

}