package ink.pmc.common.exchange.paper

import ink.pmc.common.exchange.ExchangeLobby
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player

class ExchangeLobbyImpl(override val world: World, override val teleportLocation: Location) : ExchangeLobby {

    override val players: List<Player>
        get() = world.players

}