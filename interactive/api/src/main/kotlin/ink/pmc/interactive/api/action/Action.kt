package ink.pmc.interactive.api.action

import ink.pmc.interactive.api.Effect
import org.bukkit.entity.Player

interface Action {

    val id: String
    val stepSequence: List<Step>
    val priority: Int
    val effect: Effect

    fun runEffect(player: Player)

}