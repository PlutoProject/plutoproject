package ink.pmc.common.exchange.paper

import ink.pmc.common.exchange.ExchangeConfig
import ink.pmc.common.utils.platform.paper
import org.bukkit.World
import org.bukkit.WorldCreator

@Suppress("UNUSED")
class ExchangeWorldLoader {

    private lateinit var _world: World
    private val worldCreator = WorldCreator(ExchangeConfig.ExchangeLobby.worldName)
    val world: World
        get() {
            if (!this::_world.isInitialized) {
                throw IllegalStateException("World not loaded")
            }
            return _world
        }

    fun load() {
        val loadedWorld = paper.createWorld(worldCreator) ?: throw IllegalStateException("Failed to load world!")
        _world = loadedWorld
    }

}