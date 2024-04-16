package ink.pmc.common.member.api.velocity

import com.velocitypowered.api.proxy.Player
import ink.pmc.common.member.api.Member
import ink.pmc.common.member.api.MemberService
import ink.pmc.common.utils.platform.proxy
import kotlinx.coroutines.runBlocking

@Suppress("UNUSED")
val Member.player: Player?
    get() {
        val optional = proxy.getPlayer(this.id)

        if (!optional.isEmpty) {
            return optional.get()
        }

        return null
    }

val Player.member: Member
    get() = runBlocking { MemberService.lookup(uniqueId)!! }

suspend fun Player.member(): Member {
    return MemberService.lookup(uniqueId)!!
}