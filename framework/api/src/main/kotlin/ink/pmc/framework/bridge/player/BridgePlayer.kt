package ink.pmc.framework.bridge.player

import ink.pmc.framework.bridge.world.BridgeLocation
import ink.pmc.framework.bridge.world.BridgeWorld
import ink.pmc.framework.bridge.world.Localizable
import java.util.*

interface BridgePlayer : BridgeSender, Localizable {
    val uniqueId: UUID
    val name: String
    val isOnline: Boolean

    fun teleport(location: BridgeLocation)

    fun teleport(world: BridgeWorld)

    fun teleport(localizable: Localizable)
}