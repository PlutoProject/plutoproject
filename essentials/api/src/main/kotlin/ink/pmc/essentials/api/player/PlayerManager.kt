package ink.pmc.essentials.api.player

import org.bukkit.entity.Player

@Suppress("UNUSED")
interface PlayerManager {

    fun toggleInvisibility(player: Player, prompt: Boolean = true)

    fun setInvisibility(player: Player, invisible: Boolean, prompt: Boolean = true)

    fun isInvisible(player: Player): Boolean

    fun toggleFly(player: Player, prompt: Boolean = true)

    fun setFly(player: Player, flyable: Boolean, prompt: Boolean = true)

    fun isFlyable(player: Player): Boolean

}