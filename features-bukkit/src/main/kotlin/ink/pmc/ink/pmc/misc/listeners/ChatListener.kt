package ink.pmc.misc.listeners

import ink.pmc.misc.handleChatFormat
import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

@Suppress("UNUSED")
object ChatListener : Listener {

    @EventHandler
    fun AsyncChatEvent.e() {
        handleChatFormat(this)
    }

}