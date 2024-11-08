package ink.pmc.framework.bridge.world

interface BridgeLocation : WorldElement {
    val x: Double
    val y: Double
    val z: Double
    val yaw: Float
    val pitch: Float
}