package ink.pmc.member.adapter

import com.mongodb.client.model.Filters.eq
import com.velocitypowered.api.event.player.GameProfileRequestEvent
import com.velocitypowered.api.util.GameProfile
import ink.pmc.member.memberService
import ink.pmc.member.serverLogger
import ink.pmc.framework.utils.bedrock.xuid
import kotlinx.coroutines.flow.firstOrNull

object BedrockAdapter : AuthAdapter {

    override suspend fun adapt(event: GameProfileRequestEvent) {
        val profile = event.gameProfile
        val uuid = profile.id
        val beAccount = memberService.bedrockAccounts.find(eq("xuid", uuid.xuid)).firstOrNull()
            ?: return
        val member = memberService.lookup(beAccount.linkedWith)!!
        val newProfile = GameProfile(member.id, member.rawName, profile.properties)
        event.gameProfile = newProfile
        serverLogger.info("Adapted bedrock profile for $uuid")
    }

}