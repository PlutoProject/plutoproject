package ink.pmc.common.member

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import com.velocitypowered.api.event.connection.PostLoginEvent
import ink.pmc.common.utils.concurrent.submitAsync
import ink.pmc.common.utils.currentUnixTimestamp

@Suppress("UNUSED")
object VelocityPlayerListener {

    @Subscribe
    fun postLoginEvent(event: PostLoginEvent) {
        submitAsync {
            val player = event.player
            val uuid = player.uniqueId

            if (!memberManager.exist(uuid)) {
                player.disconnect(NOT_WHITELISTED)
                return@submitAsync
            }

            val member = memberManager.get(uuid)!!

            member.sync()
            member.lastJoinTime = currentUnixTimestamp
            player.startPlay()
            member.update()
        }
    }

    @Subscribe
    fun disconnectEvent(event: DisconnectEvent) {
        submitAsync {
            val player = event.player
            val uuid = player.uniqueId

            if (!memberManager.exist(uuid)) {
                return@submitAsync
            }

            val member = memberManager.get(uuid)!!

            member.sync()
            member.lastQuitTime = currentUnixTimestamp
            member.increasePlayTime(player.stopPlay())
            member.update()
        }
    }

}