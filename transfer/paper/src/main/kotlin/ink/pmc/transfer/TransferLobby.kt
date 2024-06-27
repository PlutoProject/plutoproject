package ink.pmc.transfer

import com.electronwill.nightconfig.core.Config
import ink.pmc.transfer.lobby.PortalManager
import org.bukkit.Bukkit
import org.bukkit.GameRule
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.event.Listener

class TransferLobby(private val config: Config) {

    private val worldName = config.get<String>("world")
    private val world = loadWorld(worldName).apply { initWorldEnvironment(this) }
    val portalManager = PortalManager(config.get("portal"), world)

    object Listeners : Listener {
    }

    private fun initWorldEnvironment(world: World) {
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false)
        world.setGameRule(GameRule.DO_MOB_LOOT, false)
        world.setGameRule(GameRule.MOB_GRIEFING, false)
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false)
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false)
        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false)
        world.time = 1000
    }

    private fun loadWorld(name: String): World {
        return Bukkit.createWorld(WorldCreator.name(name))
            ?: throw IllegalStateException("Failed to load transfer world!")
    }

}