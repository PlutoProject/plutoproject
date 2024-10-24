package ink.pmc.framework.options.listeners

import com.velocitypowered.api.event.PostOrder
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import com.velocitypowered.api.event.connection.PostLoginEvent
import ink.pmc.framework.options.OptionsManager
import org.koin.core.component.KoinComponent

@Suppress("UNUSED", "UNUSED_PARAMETER", "UnusedReceiverParameter")
object VelocityOptionsListener : KoinComponent {
    @Subscribe(order = PostOrder.FIRST)
    suspend fun PostLoginEvent.e() {
        OptionsManager.getOptions(player.uniqueId)
    }

    @Subscribe(order = PostOrder.LAST)
    suspend fun DisconnectEvent.e() {
        if (!OptionsManager.isPlayerLoaded(player.uniqueId)) return
        OptionsManager.save(player.uniqueId)
        OptionsManager.unloadPlayer(player.uniqueId)
    }
}