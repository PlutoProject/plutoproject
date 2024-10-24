package ink.pmc.member

import com.mongodb.client.model.Filters.eq
import ink.pmc.member.api.AuthType
import ink.pmc.member.bedrock.newLinkedPlayerInstance
import ink.pmc.framework.utils.bedrock.xuid
import ink.pmc.framework.utils.concurrent.submitAsync
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.future.asCompletableFuture
import org.geysermc.floodgate.api.link.LinkRequestResult
import org.geysermc.floodgate.api.link.PlayerLink
import org.geysermc.floodgate.util.LinkedPlayer
import java.util.*
import java.util.concurrent.CompletableFuture

object MemberPlayerLink : PlayerLink {

    override fun load() {
        serverLogger.info("MemberPlayerLink loaded.")
    }

    override fun getLinkedPlayer(bedrockId: UUID) = submitAsync<LinkedPlayer?> {
        val xuid = bedrockId.xuid
        val bedrockAccountStorage = memberService.bedrockAccounts.find(eq("xuid", xuid)).firstOrNull()
            ?: return@submitAsync null
        val member = memberService.lookup(bedrockAccountStorage.linkedWith)!!

        if (member.authType == AuthType.BEDROCK_ONLY) {
            return@submitAsync null
        }

        serverLogger.info("MemberPlayerLink - Get a linked player: ${member.rawName}, ${member.id}, $bedrockId")
        newLinkedPlayerInstance(member.rawName, member.id, bedrockId)
    }.asCompletableFuture()

    override fun isLinkedPlayer(playerId: UUID) = submitAsync<Boolean> {
        val xuid = playerId.xuid
        val member = memberService.lookup(playerId)
        val bedrockAccountStorage = memberService.bedrockAccounts.find(eq("xuid", xuid)).firstOrNull()
        val beMember = if (bedrockAccountStorage != null) {
            memberService.lookup(bedrockAccountStorage.linkedWith)!!
        } else {
            null
        }

        if (member != null && member.authType != AuthType.BEDROCK_ONLY && member.bedrockAccount != null) {
            serverLogger.info("MemberPlayerLink - Java Edition player $playerId is a linked player.")
            return@submitAsync true
        }

        if (bedrockAccountStorage != null && beMember != null && beMember.authType != AuthType.BEDROCK_ONLY) {
            serverLogger.info("MemberPlayerLink - Bedrock Edition player $playerId is a linked player.")
            return@submitAsync true
        }

        return@submitAsync false
    }.asCompletableFuture()

    override fun linkPlayer(
        bedrockId: UUID,
        javaId: UUID,
        username: String
    ): CompletableFuture<Void> {
        return CompletableFuture.completedFuture(null)
    }

    override fun unlinkPlayer(javaId: UUID): CompletableFuture<Void> {
        return CompletableFuture.completedFuture(null)
    }

    override fun createLinkRequest(javaId: UUID, javaUsername: String, bedrockUsername: String): CompletableFuture<*> {
        return CompletableFuture.completedFuture(null)
    }

    override fun verifyLinkRequest(
        bedrockId: UUID,
        javaUsername: String,
        bedrockUsername: String,
        code: String
    ): CompletableFuture<LinkRequestResult> {
        return CompletableFuture.completedFuture(null)
    }

    override fun getName(): String {
        return "MemberPlayerLink"
    }

    override fun isEnabled(): Boolean {
        return true
    }

    override fun getVerifyLinkTimeout(): Long {
        return 0
    }

    override fun isAllowLinking(): Boolean {
        return false
    }

    override fun stop() {
        serverLogger.info("MemberPlayerLink stopped.")
    }

}