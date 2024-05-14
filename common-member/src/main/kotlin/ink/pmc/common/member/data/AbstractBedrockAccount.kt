package ink.pmc.common.member.data

import ink.pmc.common.member.api.BedrockAccount
import ink.pmc.common.member.storage.BedrockAccountStorage
import ink.pmc.common.member.storage.Storable

abstract class AbstractBedrockAccount : BedrockAccount, Storable<BedrockAccountStorage> {

    abstract var storage: BedrockAccountStorage

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