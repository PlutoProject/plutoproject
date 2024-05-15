package ink.pmc.common.member.data

import ink.pmc.common.member.api.Member
import ink.pmc.common.member.storage.BedrockAccountBean

class BedrockAccountImpl(override val linkedWith: Member, override var bean: BedrockAccountBean) :
    AbstractBedrockAccount() {

    override val id: Long = bean.id
    override val xuid: String = bean.xuid
    override var gamertag: String = bean.gamertag

    override fun reload(storage: BedrockAccountBean) {
        gamertag = storage.gamertag
        this.bean = storage
    }

    override fun createBean(): BedrockAccountBean {
        return bean.copy(
            id = this.id,
            linkedWith = this.linkedWith.uid,
            xuid = this.xuid,
            gamertag = this.gamertag,
            new = false
        )
    }

}