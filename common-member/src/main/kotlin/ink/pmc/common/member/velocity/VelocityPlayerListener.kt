package ink.pmc.common.member.velocity

import com.mongodb.client.model.Filters.eq
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import com.velocitypowered.api.event.connection.PostLoginEvent
import com.velocitypowered.api.event.player.GameProfileRequestEvent
import com.velocitypowered.api.event.player.ServerConnectedEvent
import ink.pmc.common.member.MEMBER_NOT_WHITELISTED
import ink.pmc.common.member.MEMBER_NOT_WHITELISTED_BE
import ink.pmc.common.member.adapter.BedrockAdapter
import ink.pmc.common.member.adapter.LittleSkinAdapter
import ink.pmc.common.member.api.AuthType
import ink.pmc.common.member.memberService
import ink.pmc.common.member.session.SessionType
import ink.pmc.common.member.sessionService
import ink.pmc.common.utils.bedrock.disconnect
import ink.pmc.common.utils.bedrock.isBedrockSession
import ink.pmc.common.utils.bedrock.xuid
import ink.pmc.common.utils.concurrent.io
import kotlinx.coroutines.flow.firstOrNull
import java.time.Instant
import java.util.*

@Suppress("UNUSED")
object VelocityPlayerListener {

    @Subscribe
    suspend fun postLoginEvent(event: PostLoginEvent) = io {
        val player = event.player
        val uuid = fallbackId(player.uniqueId)

        if (!memberService.isWhitelisted(uuid)) {
            player.disconnect(MEMBER_NOT_WHITELISTED, MEMBER_NOT_WHITELISTED_BE)
            return@io
        }

        memberService.modifier(uuid, true)!!.lastJoinedAt(Instant.now())
        memberService.update(uuid)
    }

    @Subscribe
    suspend fun disconnectEvent(event: DisconnectEvent) = io {
        val player = event.player
        val uuid = player.uniqueId

        if (!memberService.exist(uuid)) {
            return@io
        }

        memberService.modifier(uuid, true)!!.lastQuitedAt(Instant.now())
        memberService.update(uuid)

        sessionService.bedrockSessions.remove(uuid)
        sessionService.littleSkinSession.remove(uuid)
    }

    @Subscribe
    suspend fun gameProfileRequestEvent(event: GameProfileRequestEvent) = io {
        val profile = event.gameProfile
        val originalId = profile.id
        val uuid = fallbackId(originalId)

        if (!memberService.exist(uuid)) {
            return@io
        }

        val member = memberService.lookup(uuid)!!.refresh()!!

        if (isBedrockSession(originalId) && originalId != uuid) {
            BedrockAdapter.adapt(event)
            return@io
        }

        if (isBedrockSession(uuid)) {
            sessionService.bedrockSessions.add(uuid)
            return@io
        }

        if (member.authType == AuthType.LITTLESKIN) {
            // LittleSkinAdapter.adapt(event)
            sessionService.littleSkinSession.add(uuid)
            return@io
        }
    }

    @Subscribe
    fun serverConnectedEvent(event: ServerConnectedEvent) {
        val player = event.player

        if (sessionService.isBedrockSession(player.uniqueId)) {
            velocitySessionService.messageAdd(SessionType.BEDROCK, player.uniqueId)
            return
        }

        if (sessionService.isLittleSkinSession(player.uniqueId)) {
            velocitySessionService.messageAdd(SessionType.LITTLESKIN, player.uniqueId)
            return
        }
    }

    private suspend fun fallbackId(uuid: UUID): UUID {
        val beAccount = memberService.bedrockAccounts.find(eq("xuid", uuid.xuid)).firstOrNull()

        val fallbackId = if (isBedrockSession(uuid) && beAccount != null) {
            memberService.lookup(beAccount.linkedWith)!!.id
        } else {
            uuid
        }

        return fallbackId
    }

}