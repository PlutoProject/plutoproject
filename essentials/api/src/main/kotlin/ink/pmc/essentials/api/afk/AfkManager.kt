package ink.pmc.essentials.api.afk

import org.bukkit.entity.Player
import kotlin.time.Duration

interface AfkManager {

    val afkList: Collection<Player>
    val idleDuration: Duration

    fun isAfk(player: Player): Boolean

    fun set(player: Player, state: Boolean)

    fun toggle(player: Player)

}