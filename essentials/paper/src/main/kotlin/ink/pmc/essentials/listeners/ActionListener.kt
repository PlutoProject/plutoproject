package ink.pmc.essentials.listeners

import ink.pmc.essentials.api.teleport.TeleportManager
import ink.pmc.essentials.config.EssentialsConfig
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject

@Suppress("UNUSED", "UnusedReceiverParameter")
object ActionListener : Listener, KoinComponent {
    private val config by lazy { get<EssentialsConfig>().action }
    private val teleportManager by inject<TeleportManager>()

    /*
    * Java 版菜单动作
    * Shift + F
    * */
    @EventHandler
    fun PlayerSwapHandItemsEvent.e() {
        if (!config.sneakSwapMenu) return
        if (!player.isSneaking) return
        isCancelled = true
        player.performCommand("plutoproject_menu:menu")
    }
}