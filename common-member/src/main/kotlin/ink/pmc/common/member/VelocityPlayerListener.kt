package ink.pmc.common.member

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import com.velocitypowered.api.event.connection.PostLoginEvent
import ink.pmc.common.utils.currentUnixTimestamp
import kotlinx.coroutines.runBlocking

@Suppress("UNUSED")
object VelocityPlayerListener {

    @Subscribe
    fun postLoginEvent(event: PostLoginEvent) {
        runBlocking {
            val player = event.player
            val uuid = player.uniqueId

            if (!memberManager.exist(uuid)) {
                player.disconnect(NOT_WHITELISTED)
                return@runBlocking
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
        runBlocking {
            val player = event.player
            val uuid = player.uniqueId

            if (!memberManager.exist(uuid)) {
                return@runBlocking
            }

            val member = memberManager.get(uuid)!!

            member.sync()
            member.lastQuitTime = currentUnixTimestamp
            member.increasePlayTime(player.stopPlay())
            member.update()
        }
    }

}