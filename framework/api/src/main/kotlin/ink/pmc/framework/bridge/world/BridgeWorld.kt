package ink.pmc.framework.bridge.world

import ink.pmc.framework.bridge.player.PlayerLookup
import ink.pmc.framework.bridge.server.ServerElement

interface BridgeWorld : PlayerLookup, ServerElement {
    val name: String
    val alias: String?
    val aliasOrName: String get() = alias ?: name
    val spawnPoint: BridgeLocation

    fun getLocation(
        x: Double,
        y: Double,
        z: Double,
        yaw: Float = 0.0F,
        pitch: Float = 0.0F
    ): BridgeLocation
}