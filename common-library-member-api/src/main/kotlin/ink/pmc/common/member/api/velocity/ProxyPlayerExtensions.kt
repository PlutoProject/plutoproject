package ink.pmc.common.member.api.velocity

import com.velocitypowered.api.proxy.Player
import ink.pmc.common.member.api.Member
import ink.pmc.common.member.api.MemberAPI
import kotlinx.coroutines.runBlocking

@Suppress("UNUSED")
val Player.member: Member?
    get() {
        val player = this
        return runBlocking { MemberAPI.instance.memberManager.get(player.uniqueId) }
    }

@Suppress("UNUSED")
suspend fun Player.getMember(): Member? {
    return MemberAPI.instance.memberManager.get(this.uniqueId)
}