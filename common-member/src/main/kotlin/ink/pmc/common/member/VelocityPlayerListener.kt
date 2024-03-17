package ink.pmc.common.member

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import com.velocitypowered.api.event.connection.PostLoginEvent
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

@Suppress("UNUSED")
@OptIn(DelicateCoroutinesApi::class)
object VelocityPlayerListener {

    @Subscribe
    fun postLoginEvent(event: PostLoginEvent) {
        GlobalScope.launch {
            val player = event.player
            val uuid = player.uniqueId

            if (!memberManager.exist(uuid)) {
                player.disconnect(NOT_WHITELISTED)
                return@launch
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
        GlobalScope.launch {
            val player = event.player
            val uuid = player.uniqueId

            if (!memberManager.exist(uuid)) {
                return@launch
            }

            val member = memberManager.get(uuid)!!

            member.sync()
            member.lastQuitTime = Date()
            member.increasePlayTime(player.stopPlay())
            member.update()
        }
    }

}