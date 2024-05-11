package ink.pmc.common.member.data

import ink.pmc.common.member.api.BedrockAccount
import ink.pmc.common.member.storage.BedrockAccountStorage

abstract class AbstractBedrockAccount : BedrockAccount {

    abstract val storage: BedrockAccountStorage

    override fun equals(other: Any?): Boolean {
        if (other !is BedrockAccount) {
            return false
        }

        return other.id == id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    abstract fun reload(storage: BedrockAccountStorage)

}