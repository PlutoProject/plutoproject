package ink.pmc.member.paper

import ink.pmc.member.bedrock.removeFloodgatePlayer
import ink.pmc.member.memberService
import ink.pmc.utils.bedrock.uuid
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

@Suppress("UNUSED")
object PaperPlayerListener : Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    suspend fun playerJoinEvent(event: PlayerJoinEvent) {
        val player = event.player
        val uuid = player.uniqueId

        if (!memberService.exist(uuid)) {
            return
        }

        memberService.lookup(uuid)
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    suspend fun playerQuitEvent(event: PlayerQuitEvent) {
        val player = event.player
        val uuid = player.uniqueId

        removeFloodgatePlayer(uuid)

        if (!memberService.exist(player.uniqueId)) {
            return
        }

        val member = memberService.lookup(player.uniqueId)!!

        if (member.bedrockAccount != null) {
            removeFloodgatePlayer(member.bedrockAccount!!.xuid.uuid!!)
        }

        // 清理缓存的 Member
        if (memberService.loadedMembers.getIfPresent(member.uid) != null) {
            return
        }

        memberService.loadedMembers.synchronous().invalidate(member.uid)
    }

}