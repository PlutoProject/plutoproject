package ink.pmc.common.member.paper

import ink.pmc.common.member.bedrock.removeFloodgatePlayer
import ink.pmc.common.member.memberService
import ink.pmc.common.utils.bedrock.uuid
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

@Suppress("UNUSED")
object PaperPlayerListener : Listener {

    @EventHandler
    suspend fun playerQuitEvent(event: PlayerQuitEvent) {
        val player = event.player
        val uuid = player.uniqueId

        removeFloodgatePlayer(uuid)

        if (!memberService.exist(player.uniqueId)) {
            return
        }

        val member = memberService.lookup(player.uniqueId)!!.refresh()!!

        if (member.bedrockAccount != null) {
            removeFloodgatePlayer(member.bedrockAccount!!.xuid.uuid!!)
        }
    }

}