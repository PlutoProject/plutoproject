package ink.pmc.essentials.api.teleport

import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent

@Suppress("UNUSED")
class EssentialsTeleportEvent(
    player: Player,
    val destination: Location,
    val options: TeleportOptions
) : PlayerEvent(player, true) {

    private var _isDenied = false
    private var _deniedReason: Component? = null

    val isDenied: Boolean
        get() = _isDenied
    val deniedReason: Component?
        get() = _deniedReason

    private companion object {
        val handlers = HandlerList()
    }

    fun denied(reason: Component?) {
        _isDenied = true
        _deniedReason = reason
    }

    override fun getHandlers(): HandlerList {
        return Companion.handlers
    }

}