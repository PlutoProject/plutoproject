package ink.pmc.exchange.api

import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player

interface ExchangeLobby {

    val world: World
    val teleportLocation: Location
    val players: List<Player>

}