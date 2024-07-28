package ink.pmc.essentials.teleport.random

import ink.pmc.essentials.api.teleport.ManagerState
import ink.pmc.essentials.api.teleport.random.*
import ink.pmc.essentials.config.EssentialsConfig
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import java.util.*

class RandomTeleportManagerImpl : RandomTeleportManager, KoinComponent {

    private val conf = get<EssentialsConfig>().RandomTeleport()

    override val cacheTasks: MutableList<RandomTeleportCacheTask> = mutableListOf()
    override val teleportQueue: Queue<RandomTeleportTask> = LinkedList()
    override val caches: MutableList<RandomTeleportCache> = mutableListOf()
    override val maxCaches: Int = conf.chunkCacheMax
    override val defaultOptions: Map<World, RandomTeleportOptions>
        get() = TODO("Not yet implemented")
    override val blacklistedWorlds: Collection<World> = conf.blacklistedWorlds
    override var tickCount: Long = 0L
    override var lastTickTime: Long = 0L
    override var state: ManagerState = ManagerState.IDLE

    override fun getRandomTeleportOptions(world: World): RandomTeleportOptions {
        TODO("Not yet implemented")
    }

    override fun pollCache(world: World): Location? {
        TODO("Not yet implemented")
    }

    override fun searchSafeLocation(world: World, options: RandomTeleportOptions?): Location? {
        TODO("Not yet implemented")
    }

    override fun launch(
        player: Player,
        world: World,
        options: RandomTeleportOptions?,
        prompt: Boolean
    ): RandomTeleportTask {
        TODO("Not yet implemented")
    }

    override suspend fun launchSuspend(
        player: Player,
        world: World,
        options: RandomTeleportOptions?,
        prompt: Boolean
    ): RandomTeleportTask {
        TODO("Not yet implemented")
    }

    override fun cancel(player: Player) {
        TODO("Not yet implemented")
    }

    override fun isBlacklisted(world: World): Boolean {
        TODO("Not yet implemented")
    }

    override fun tick() {
        TODO("Not yet implemented")
    }

}