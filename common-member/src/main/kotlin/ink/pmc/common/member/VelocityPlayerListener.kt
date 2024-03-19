package ink.pmc.common.member

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import com.velocitypowered.api.event.connection.PostLoginEvent
import ink.pmc.common.utils.concurrent.async
import java.util.*

@Suppress("UNUSED")
object VelocityPlayerListener {

    @Subscribe
    fun postLoginEvent(event: PostLoginEvent) {
        async {
            val player = event.player
            val uuid = player.uniqueId

            if (!memberManager.exist(uuid)) {
                player.disconnect(NOT_WHITELISTED)
                return@async
            }

            val member = memberManager.get(uuid)!!

            member.sync()
            member.lastJoinTime = Date()
            player.startPlay()
            member.update()
        }
    }

    @Subscribe
    fun disconnectEvent(event: DisconnectEvent) {
        async {
            val player = event.player
            val uuid = player.uniqueId

            if (!memberManager.exist(uuid)) {
                return@async
            }

            val member = memberManager.get(uuid)!!

            member.sync()
            member.lastQuitTime = Date()
            member.increasePlayTime(player.stopPlay())
            member.update()
        }
    }

}