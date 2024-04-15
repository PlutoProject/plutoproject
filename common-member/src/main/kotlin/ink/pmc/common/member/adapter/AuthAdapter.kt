package ink.pmc.common.member.adapter

import com.velocitypowered.api.event.player.GameProfileRequestEvent

interface AuthAdapter {

    fun adapt(event: GameProfileRequestEvent)

}