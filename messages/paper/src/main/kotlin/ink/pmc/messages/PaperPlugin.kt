package ink.pmc.messages

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin

@Suppress("UNUSED")
class PaperPlugin : JavaPlugin() {

    override fun onEnable() {
        server.pluginManager.registerEvents(PlayerListener, this)
    }

    object PlayerListener: Listener {
        @EventHandler
        fun PlayerJoinEvent.e() {
            joinMessage(null)
        }

        @EventHandler
        fun PlayerQuitEvent.e() {
            quitMessage(null)
        }
    }

}