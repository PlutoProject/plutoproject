package ink.pmc.common.member.data

import ink.pmc.common.member.AbstractMemberService
import ink.pmc.common.member.api.Member
import ink.pmc.common.member.storage.BedrockAccountStorage
import kotlinx.coroutines.runBlocking

class BedrockAccountImpl(private val service: AbstractMemberService, override val storage: BedrockAccountStorage) :
    AbstractBedrockAccount() {

    override val id: Long
        get() = storage.id
    override val linkedWith: Member = runBlocking { service.lookup(storage.linkedWith)!! }
    override val xuid: String
        get() = storage.xuid
    override val gamertag: String
        get() = storage.gamertag

}