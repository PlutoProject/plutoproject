package ink.pmc.member.api.paper

import ink.pmc.member.api.Member
import ink.pmc.member.api.MemberService
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer

@Suppress("UNUSED")
val Member.player: OfflinePlayer
    get() = Bukkit.getOfflinePlayer(this.id)

suspend fun OfflinePlayer.member(): Member {
    return MemberService.lookup(uniqueId)!!
}

suspend fun OfflinePlayer.memberOrNull(): Member? {
    return MemberService.lookup(uniqueId)
}