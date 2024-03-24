package ink.pmc.common.member.api.velocity

import com.velocitypowered.api.proxy.Player
import ink.pmc.common.member.api.Member
import ink.pmc.common.member.api.MemberAPI
import ink.pmc.common.utils.concurrent.submitAsync
import kotlinx.coroutines.future.asCompletableFuture

@Suppress("UNUSED")
val Player.member: Member?
    get() {
        val player = this

        val value = submitAsync<Member?> {
            MemberAPI.instance.memberManager.get(player.uniqueId)
        }

        return value.asCompletableFuture().join()
    }

@Suppress("UNUSED")
suspend fun Player.getMember(): Member? {
    return MemberAPI.instance.memberManager.get(this.uniqueId)
}