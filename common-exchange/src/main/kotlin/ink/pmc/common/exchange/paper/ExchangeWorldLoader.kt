package ink.pmc.common.exchange.paper

import ink.pmc.common.utils.platform.paper
import org.bukkit.World
import org.bukkit.WorldCreator
import java.io.File

@Suppress("UNUSED")
class ExchangeWorldLoader(worldFolder: File) {

    private val worldName = worldFolder.relativeTo(paper.worldContainer).toPath().toString()
    private lateinit var _world: World
    private val worldCreator = WorldCreator(worldName)
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