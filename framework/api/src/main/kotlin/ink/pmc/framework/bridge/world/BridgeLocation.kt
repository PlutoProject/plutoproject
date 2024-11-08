package ink.pmc.framework.bridge.world

import ink.pmc.framework.bridge.server.BridgeServer

interface BridgeLocation {
    val server: BridgeServer
    val world: BridgeWorld
    val x: Double
    val y: Double
    val z: Double
    val yaw: Float
    val pitch: Float
}