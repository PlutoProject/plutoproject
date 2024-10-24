package ink.pmc.framework.visual.display.text

import com.destroystokyo.paper.event.server.ServerTickEndEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Suppress("UNUSED", "UnusedReceiverParameter")
object TextDisplayListener : Listener, KoinComponent {
    private val manager by inject<TextDisplayManager>()

    @EventHandler
    fun ServerTickEndEvent.e() {
        manager.renderAll()
    }
}