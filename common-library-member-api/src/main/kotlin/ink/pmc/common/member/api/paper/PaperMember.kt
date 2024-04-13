package ink.pmc.common.member.api.paper

import ink.pmc.common.member.api.Member
import ink.pmc.common.member.api.MemberService
import kotlinx.coroutines.runBlocking
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer

@Suppress("UNUSED")
val Member.player: OfflinePlayer
    get() = Bukkit.getOfflinePlayer(this.id)

val OfflinePlayer.member: Member
    get() = runBlocking { MemberService.lookup(uniqueId)!! }

suspend fun OfflinePlayer.member(): Member {
    return MemberService.lookup(uniqueId)!!
}