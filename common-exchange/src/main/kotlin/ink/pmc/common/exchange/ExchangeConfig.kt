package ink.pmc.common.exchange

import org.bukkit.Material

@Suppress("UNUSED")
object ExchangeConfig {

    lateinit var identity: String

    object ExchangeLobby {
        lateinit var worldName: String
        object TeleportLocation {
            var x: Double = 0.0
            var y: Double = 128.0
            var z: Double = 0.0
            var yaw: Float = 0.0F
            var pitch: Float = 0.0F
        }
    }

    object Tickets {
        var daily: Int = 0
    }

    object AvailableItems {
        lateinit var materials: List<Material>
    }

}