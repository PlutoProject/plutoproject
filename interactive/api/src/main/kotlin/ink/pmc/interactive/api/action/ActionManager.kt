package ink.pmc.interactive.api.action

import ink.pmc.utils.inject.inlinedGet
import org.bukkit.entity.Player

interface ActionManager {

    companion object : ActionManager by inlinedGet()

    val globalActions: Collection<Action>

    fun registerGlobalAction(action: Action)

    fun isGlobalActionRegistered(id: String): Boolean

    fun unregisterGlobalAction(id: String)

    fun registerPlayerAction(player: Player)

    fun isPlayerActionRegistered(player: Player, id: String)

    fun unregisterPlayerAction(player: Player, id: String)

}