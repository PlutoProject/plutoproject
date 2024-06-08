package ink.pmc.member.adapter

import com.velocitypowered.api.event.player.GameProfileRequestEvent

interface AuthAdapter {

    suspend fun adapt(event: GameProfileRequestEvent)

}