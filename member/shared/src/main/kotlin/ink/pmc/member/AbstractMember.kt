package ink.pmc.member

import ink.pmc.member.api.Member
import ink.pmc.member.data.AbstractBedrockAccount
import ink.pmc.member.data.AbstractDataContainer
import ink.pmc.member.storage.MemberBean
import ink.pmc.member.storage.Storable

abstract class AbstractMember : Member, Storable<MemberBean> {

    abstract var bean: MemberBean
    abstract override val bedrockAccount: AbstractBedrockAccount?
    abstract override val dataContainer: AbstractDataContainer

    override fun equals(other: Any?): Boolean {
        if (other !is Member) {
            return false
        }

        return other.uid == uid
    }

    override fun hashCode(): Int {
        return bean.hashCode()
    }
}