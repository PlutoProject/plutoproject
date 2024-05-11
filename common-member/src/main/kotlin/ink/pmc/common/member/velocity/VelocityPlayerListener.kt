package ink.pmc.common.member.velocity

import com.mongodb.client.model.Filters.eq
import com.velocitypowered.api.event.PostOrder
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import com.velocitypowered.api.event.connection.PostLoginEvent
import com.velocitypowered.api.event.player.GameProfileRequestEvent
import ink.pmc.common.member.MEMBER_NAME_CHANGED
import ink.pmc.common.member.MEMBER_NOT_WHITELISTED
import ink.pmc.common.member.MEMBER_NOT_WHITELISTED_BE
import ink.pmc.common.member.adapter.BedrockAdapter
import ink.pmc.common.member.api.AuthType
import ink.pmc.common.member.bedrock.removeFloodgatePlayer
import ink.pmc.common.member.memberService
import ink.pmc.common.utils.bedrock.isFloodgatePlayer
import ink.pmc.common.utils.bedrock.uuid
import ink.pmc.common.utils.bedrock.xuid
import ink.pmc.common.utils.chat.replace
import ink.pmc.common.utils.concurrent.io
import ink.pmc.common.utils.visual.mochaYellow
import kotlinx.coroutines.flow.firstOrNull
import net.kyori.adventure.text.Component
import java.time.Instant
import java.util.*

@Suppress("UNUSED")
object VelocityPlayerListener {

    private fun deniedPrompt(uuid: UUID): Component = if (isFloodgatePlayer(uuid)) {
        MEMBER_NOT_WHITELISTED_BE
    } else {
        MEMBER_NOT_WHITELISTED
    }

    @Subscribe
    suspend fun postLoginEvent(event: PostLoginEvent) {
        val player = event.player
        val uuid = fallbackId(player.uniqueId)

        if (!memberService.isWhitelisted(uuid)) {
            player.disconnect(deniedPrompt(uuid))
            return
        }

        val member = memberService.lookup(uuid)!!.reload()!!

        if (member.rawName != player.username) {
            member.modifier.name(player.username)
            player.sendMessage(
                MEMBER_NAME_CHANGED
                    .replace("<oldName>", Component.text(member.rawName).color(mochaYellow))
                    .replace("<newName>", Component.text(player.username).color(mochaYellow))
            )
        }

        member.modifier.lastJoinedAt(Instant.now())
        member.save()
    }

    @Subscribe
    suspend fun disconnectEvent(event: DisconnectEvent) {
        val player = event.player
        val uuid = player.uniqueId

        removeFloodgatePlayer(uuid)

        if (!memberService.exist(uuid)) {
            return
        }

        val member = memberService.lookup(uuid)!!.reload()!!
        member.modifier.lastQuitedAt(Instant.now())
        memberService.save(uuid)

        if (member.bedrockAccount != null) {
            removeFloodgatePlayer(member.bedrockAccount!!.xuid.uuid!!)
        }
    }

    @Subscribe(order = PostOrder.FIRST)
    suspend fun gameProfileRequestEvent(event: GameProfileRequestEvent) = io {
        val profile = event.gameProfile
        val originalId = profile.id
        val uuid = fallbackId(originalId)

        if (!memberService.exist(uuid)) {
            return@io
        }

        val member = memberService.lookup(uuid)!!.reload()!!

        if (isFloodgatePlayer(originalId) && originalId != uuid) {
            BedrockAdapter.adapt(event)
            return@io
        }

        if (member.authType == AuthType.LITTLESKIN) {
            // LittleSkinAdapter.adapt(event)
            return@io
        }
    }

    private suspend fun fallbackId(uuid: UUID): UUID {
        val beAccount = memberService.bedrockAccounts.find(eq("xuid", uuid.xuid)).firstOrNull()

        val fallbackId = if (isFloodgatePlayer(uuid) && beAccount != null) {
            memberService.lookup(beAccount.linkedWith)!!.id
        } else {
            uuid
        }

        return fallbackId
    }

}