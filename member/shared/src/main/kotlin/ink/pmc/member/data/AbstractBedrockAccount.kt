package ink.pmc.member.data

import ink.pmc.member.api.BedrockAccount
import ink.pmc.member.storage.BedrockAccountBean
import ink.pmc.member.storage.Storable

abstract class AbstractBedrockAccount : BedrockAccount, Storable<BedrockAccountBean> {

    abstract var bean: BedrockAccountBean

    override fun equals(other: Any?): Boolean {
        if (other !is BedrockAccount) {
            return false
        }

        return other.id == id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}