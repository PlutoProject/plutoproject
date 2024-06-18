package ink.pmc.transfer

import com.electronwill.nightconfig.core.Config
import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import org.bukkit.*
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

class TransferLobby(
    server: Server,
    plugin: JavaPlugin,
    private val config: Config
) {

    private val worldName = config.get<String>("world")
    private val world = loadWorld(worldName).apply { initWorldEnvironment(this) }

    init {
        server.pluginManager.registerSuspendingEvents(Listeners, plugin)
    }

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
        return Bukkit.createWorld(WorldCreator.name(name)) ?: throw IllegalStateException("Failed to load transfer world!")
    }

}