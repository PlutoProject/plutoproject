package ink.pmc.common.member.data

import ink.pmc.common.member.AbstractMemberService
import ink.pmc.common.member.api.Member
import ink.pmc.common.member.storage.BedrockAccountStorage
import kotlinx.coroutines.runBlocking

class BedrockAccountImpl(private val service: AbstractMemberService, override val storage: BedrockAccountStorage) :
    AbstractBedrockAccount() {

    override val id: Long = storage.id
    override val linkedWith: Member by lazy { runBlocking { service.lookup(storage.linkedWith)!! } }
    override val xuid: String = storage.xuid
    override var gamertag: String = storage.gamertag

}