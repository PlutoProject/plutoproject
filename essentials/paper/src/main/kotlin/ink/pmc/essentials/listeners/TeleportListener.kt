package ink.pmc.essentials.listeners

import com.destroystokyo.paper.event.server.ServerTickEndEvent
import ink.pmc.essentials.TELEPORT_REQUEST_CANCELED_OFFLINE
import ink.pmc.essentials.api.teleport.TeleportManager
import ink.pmc.utils.chat.replace
import ink.pmc.utils.concurrent.submitSync
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Suppress("UNUSED", "UnusedReceiverParameter")
object TeleportListener : Listener, KoinComponent {

    private val teleportManager by inject<TeleportManager>()

    @EventHandler
    fun ServerTickEndEvent.e() {
        // 丢入休眠期间执行
        submitSync {
            teleportManager.tick()
        }
    }

    @EventHandler
    fun PlayerQuitEvent.e() {
        val sourceOffline = teleportManager.teleportRequests.firstOrNull { it.source == player }
        val destinationOffline = teleportManager.teleportRequests.firstOrNull { it.destination == player }

        sourceOffline?.cancel(false)
        destinationOffline?.cancel(false)

        sourceOffline?.destination?.sendMessage(
            TELEPORT_REQUEST_CANCELED_OFFLINE.replace(
                "<player>",
                sourceOffline.source.name
            )
        )
        destinationOffline?.source?.sendMessage(
            TELEPORT_REQUEST_CANCELED_OFFLINE.replace(
                "<player>",
                destinationOffline.destination.name
            )
        )
    }

}