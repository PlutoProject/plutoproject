package ink.pmc.essentials.listeners

import ink.pmc.essentials.LECT_PROTECTED_ACTION
import ink.pmc.essentials.commands.isProtected
import ink.pmc.essentials.commands.protector
import ink.pmc.essentials.commands.protectorName
import ink.pmc.utils.chat.replace
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerTakeLecternBookEvent

@Suppress("UNUSED", "UnusedReceiverParameter")
object LecternListener : Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun PlayerTakeLecternBookEvent.e() {
        if (!lectern.isProtected) {
            return
        }

        if (lectern.protector != player) {
            player.sendActionBar(LECT_PROTECTED_ACTION.replace("<player>", lectern.protectorName))
        }

        isCancelled = true
    }

}