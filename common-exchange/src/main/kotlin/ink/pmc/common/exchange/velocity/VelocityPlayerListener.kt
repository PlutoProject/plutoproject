package ink.pmc.common.exchange.velocity

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.PostLoginEvent
import ink.pmc.common.exchange.initExchangeData
import ink.pmc.common.member.api.MemberService

@Suppress("UNUSED")
object VelocityPlayerListener {

    @Subscribe
    suspend fun postConnectEvent(event: PostLoginEvent) {
        val uuid = event.player.uniqueId

        if (!MemberService.exist(uuid)) {
            return
        }

        val member = MemberService.lookup(uuid)!!.refresh()!!
        initExchangeData(member)
    }

}