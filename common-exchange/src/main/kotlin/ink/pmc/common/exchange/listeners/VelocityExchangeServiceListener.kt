package ink.pmc.common.exchange.listeners

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.PostLoginEvent
import ink.pmc.common.exchange.initExchangeData
import ink.pmc.common.exchange.utils.distributeTicket
import ink.pmc.common.member.api.MemberService
import ink.pmc.common.member.api.velocity.member

@Suppress("UNUSED")
object VelocityExchangeServiceListener {

    @Subscribe
    suspend fun postLoginEvent(event: PostLoginEvent) {
        val player = event.player
        val uuid = player.uniqueId

        if (!MemberService.exist(uuid)) {
            return
        }

        val member = MemberService.lookup(uuid)!!
        initExchangeData(member)
        distributeTicket(player.member())
        member.save()
    }

}