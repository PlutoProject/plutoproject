package ink.pmc.interactive.api

import ink.pmc.interactive.api.session.InventorySession
import ink.pmc.interactive.api.session.Session
import ink.pmc.utils.inject.inlinedGet
import org.bukkit.entity.Player

interface Interactive {

    companion object : Interactive by inlinedGet()

    fun get(player: Player): Session<*>?

    fun has(player: Player): Boolean

    fun startInventory(player: Player, contents: ComposableFunction): InventorySession

    fun remove(session: Session<*>)

    fun dispose()

}