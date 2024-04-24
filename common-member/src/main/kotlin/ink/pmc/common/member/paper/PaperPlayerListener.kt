package ink.pmc.common.member.paper

import ink.pmc.common.member.sessionService
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

@Suppress("UNUSED")
object PaperPlayerListener : Listener {

    @EventHandler
    fun playerQuitEvent(event: PlayerQuitEvent) {
        val player = event.player
        sessionService.bedrockSessions.remove(player.uniqueId)
        sessionService.littleSkinSession.remove(player.uniqueId)
    }

}