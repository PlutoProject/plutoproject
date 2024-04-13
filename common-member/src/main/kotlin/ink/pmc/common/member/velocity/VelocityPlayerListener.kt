package ink.pmc.common.member.velocity

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import com.velocitypowered.api.event.connection.PostLoginEvent
import ink.pmc.common.member.NOT_WHITELISTED
import ink.pmc.common.member.memberService
import kotlinx.coroutines.runBlocking
import java.time.Instant

@Suppress("UNUSED")
object VelocityPlayerListener {

    @Subscribe
    fun postLoginEvent(event: PostLoginEvent) {
        runBlocking {
            val player = event.player
            val uuid = player.uniqueId

            if (!memberService.isWhitelisted(uuid)) {
                player.disconnect(NOT_WHITELISTED)
                return@runBlocking
            }

            memberService.modifier(uuid, true)!!.lastJoinedAt(Instant.now())
            memberService.update(uuid)
        }
    }

    @Subscribe
    fun disconnectEvent(event: DisconnectEvent) {
        runBlocking {
            val player = event.player
            val uuid = player.uniqueId

            if (!memberService.exist(uuid)) {
                return@runBlocking
            }

            memberService.modifier(uuid, true)!!.lastQuitedAt(Instant.now())
            memberService.update(uuid)
        }
    }

}