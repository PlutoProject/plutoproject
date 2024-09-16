package ink.pmc.member.api.velocity

import com.velocitypowered.api.proxy.Player
import ink.pmc.member.api.Member
import ink.pmc.member.api.MemberService
import ink.pmc.utils.platform.proxy

@Suppress("UNUSED")
val Member.player: Player?
    get() {
        val optional = proxy.getPlayer(this.id)

        if (!optional.isEmpty) {
            return optional.get()
        }

        return null
    }

suspend fun Player.member(): Member {
    return MemberService.lookup(uniqueId)!!
}