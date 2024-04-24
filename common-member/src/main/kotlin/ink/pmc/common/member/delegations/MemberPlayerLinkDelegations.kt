package ink.pmc.common.member.delegations

import com.mongodb.client.model.Filters.eq
import ink.pmc.common.member.api.AuthType
import ink.pmc.common.member.bedrock.newLinkedPlayer
import ink.pmc.common.member.memberService
import ink.pmc.common.member.serverLogger
import ink.pmc.common.utils.bedrock.xuid
import ink.pmc.common.utils.concurrent.submitAsync
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.future.asCompletableFuture
import net.bytebuddy.implementation.bind.annotation.Argument
import java.util.*
import java.util.concurrent.CompletableFuture

@Suppress("UNUSED", "UNUSED_PARAMETER")
object MemberPlayerLinkDelegations {

    @JvmStatic
    fun load() {
        serverLogger.info("MemberPlayerLink loaded.")
    }

    @JvmStatic
    fun getLinkedPlayer(@Argument(0) bedrockId: UUID): CompletableFuture<Any?> = submitAsync<Any?> {
        val xuid = bedrockId.xuid
        val bedrockAccountStorage = memberService.bedrockAccounts.find(eq("xuid", xuid)).firstOrNull()
            ?: return@submitAsync null
        val member = memberService.lookup(bedrockAccountStorage.linkedWith)!!

        if (member.authType == AuthType.BEDROCK_ONLY) {
            return@submitAsync null
        }

        serverLogger.info("MemberPlayerLink - Get a linked player: ${member.rawName}, ${member.id}, $bedrockId")
        newLinkedPlayer(member.rawName, member.id, bedrockId)
    }.asCompletableFuture()

    @JvmStatic
    fun isLinkedPlayer(@Argument(0) playerId: UUID): CompletableFuture<Boolean> = submitAsync<Boolean> {
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

    @JvmStatic
    fun linkPlayer(
        @Argument(0) bedrockId: UUID,
        @Argument(1) javaId: UUID,
        @Argument(2) username: String
    ): CompletableFuture<Void>? {
        return null
    }

    @JvmStatic
    fun createLinkRequest(
        @Argument(0) javaId: UUID?,
        @Argument(1) javaUsername: String?,
        @Argument(2) bedrockUsername: String?
    ): CompletableFuture<*>? {
        return null
    }

    @JvmStatic
    fun verifyLinkRequest(
        @Argument(0) bedrockId: UUID?,
        @Argument(1) javaUsername: String?,
        @Argument(2) bedrockUsername: String?,
        @Argument(3) code: String?
    ): CompletableFuture<Any>? {
        return null
    }

    @JvmStatic
    fun getName(): String {
        return "MemberPlayerLink"
    }

    @JvmStatic
    fun isEnabled(): Boolean {
        return true
    }

    @JvmStatic
    fun getVerifyLinkTimeout(): Long {
        return 0
    }

    @JvmStatic
    fun isAllowLinking(): Boolean {
        return false
    }

    @JvmStatic
    fun stop() {
        serverLogger.info("MemberPlayerLink stopped.")
    }

}