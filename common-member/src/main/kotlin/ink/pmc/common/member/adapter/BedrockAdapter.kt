package ink.pmc.common.member.adapter

import com.mongodb.client.model.Filters.eq
import com.velocitypowered.api.event.player.GameProfileRequestEvent
import com.velocitypowered.api.util.GameProfile
import ink.pmc.common.member.memberService
import ink.pmc.common.utils.bedrock.xuid
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking

object BedrockAdapter : AuthAdapter {

    override fun adapt(event: GameProfileRequestEvent) {
        runBlocking {
            val profile = event.gameProfile
            val uuid = profile.id
            val beAccount = memberService.bedrockAccounts.find(eq("xuid", uuid.xuid)).firstOrNull()
                ?: return@runBlocking
            val member = memberService.lookup(beAccount.linkedWith)!!
            val newProfile = GameProfile(member.id, member.rawName, profile.properties)
            event.gameProfile = newProfile
        }
    }

}