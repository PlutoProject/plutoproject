package ink.pmc.common.exchange

@Suppress("UNUSED")
object ExchangeConfig {

    lateinit var identity: String

    object ExchangeLobby {
        lateinit var worldName: String
        object TeleportLocation {
            var x: Double = 0.0
            var y: Double = 128.0
            var z: Double = 0.0
            var yaw: Double = 0.0
            var pitch: Double = 0.0
        }
    }

}