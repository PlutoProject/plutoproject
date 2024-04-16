package ink.pmc.common.member.velocity

import com.mongodb.client.model.Filters.eq
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import com.velocitypowered.api.event.connection.PostLoginEvent
import com.velocitypowered.api.event.player.GameProfileRequestEvent
import ink.pmc.common.member.MEMBER_NOT_WHITELISTED
import ink.pmc.common.member.adapter.BedrockAdapter
import ink.pmc.common.member.adapter.LittleSkinAdapter
import ink.pmc.common.member.api.AuthType
import ink.pmc.common.member.isFloodgateSession
import ink.pmc.common.member.memberService
import ink.pmc.common.utils.bedrock.xuid
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import java.time.Instant
import java.util.*

@Suppress("UNUSED")
object VelocityPlayerListener {

    @Subscribe
    fun postLoginEvent(event: PostLoginEvent) {
        runBlocking {
            val player = event.player
            val uuid = fallbackId(player.uniqueId)

            if (!memberService.isWhitelisted(uuid)) {
                player.disconnect(MEMBER_NOT_WHITELISTED)
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

    @Subscribe
    fun gameProfileRequestEvent(event: GameProfileRequestEvent) {
        runBlocking {
            val profile = event.gameProfile
            val originalId = profile.id
            val uuid = fallbackId(originalId)

            if (!memberService.exist(uuid)) {
                return@runBlocking
            }

            val member = memberService.lookup(uuid)!!.refresh()!!

            if (isFloodgateSession(originalId) && originalId != uuid) {
                BedrockAdapter.adapt(event)
                return@runBlocking
            }

            if (member.authType == AuthType.LITTLESKIN) {
                // LittleSkinAdapter.adapt(event)
                return@runBlocking
            }
        }
    }

    private suspend fun fallbackId(uuid: UUID): UUID {
        var fallbackId = uuid
        val beAccount = memberService.bedrockAccounts.find(eq("xuid", uuid.xuid)).firstOrNull()

        if (isFloodgateSession(fallbackId) && beAccount != null) {
            fallbackId = memberService.lookup(beAccount.linkedWith)!!.id
        }

        return fallbackId
    }

}