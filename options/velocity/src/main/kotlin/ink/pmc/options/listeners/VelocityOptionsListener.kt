package ink.pmc.options.listeners

import com.velocitypowered.api.event.PostOrder
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import com.velocitypowered.api.event.connection.PostLoginEvent
import ink.pmc.options.api.OptionsManager
import org.koin.core.component.KoinComponent

@Suppress("UNUSED", "UNUSED_PARAMETER", "UnusedReceiverParameter")
object VelocityOptionsListener : KoinComponent {
    @Subscribe(order = PostOrder.FIRST)
    suspend fun PostLoginEvent.e() {
        OptionsManager.getContainer(player.uniqueId)
    }

    @Subscribe(order = PostOrder.LAST)
    suspend fun DisconnectEvent.e() {
        if (!OptionsManager.isContainerLoaded(player.uniqueId)) return
        OptionsManager.save(player.uniqueId)
        OptionsManager.unloadContainer(player.uniqueId)
    }
}