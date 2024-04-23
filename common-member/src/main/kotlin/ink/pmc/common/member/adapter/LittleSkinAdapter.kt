package ink.pmc.common.member.adapter

import com.velocitypowered.api.event.player.GameProfileRequestEvent
import com.velocitypowered.api.util.GameProfile
import ink.pmc.common.member.sessionService

object LittleSkinAdapter : AuthAdapter {

    override suspend fun adapt(event: GameProfileRequestEvent) {
        val profile = event.gameProfile
        val newProfile = GameProfile(profile.id, profile.name, profile.properties)
        event.gameProfile = newProfile
    }

}