package ink.pmc.common.member.data

import ink.pmc.common.member.api.Member
import ink.pmc.common.member.storage.BedrockAccountStorage

class BedrockAccountImpl(override val linkedWith: Member, override val storage: BedrockAccountStorage) :
    AbstractBedrockAccount() {

    override val id: Long = storage.id
    override val xuid: String = storage.xuid
    override var gamertag: String = storage.gamertag

}