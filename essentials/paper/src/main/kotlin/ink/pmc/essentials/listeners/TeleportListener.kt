package ink.pmc.essentials.listeners

import com.destroystokyo.paper.event.server.ServerTickEndEvent
import ink.pmc.essentials.api.teleport.TeleportManager
import ink.pmc.utils.concurrent.submitSync
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
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

}