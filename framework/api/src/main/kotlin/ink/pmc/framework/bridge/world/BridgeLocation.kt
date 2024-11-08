package ink.pmc.framework.bridge.world

interface BridgeLocation : WorldElement<BridgeLocation> {
    val x: Double
    val y: Double
    val z: Double
    val yaw: Float
    val pitch: Float
}