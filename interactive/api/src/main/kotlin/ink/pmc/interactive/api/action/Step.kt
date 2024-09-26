package ink.pmc.interactive.api.action

import org.bukkit.entity.Player

interface Step {

    val id: String

    fun trigger(player: Player)

    fun release(player: Player)

}