package ink.pmc.essentials.warp

import ink.pmc.essentials.api.warp.Warp
import ink.pmc.essentials.api.warp.WarpManager
import ink.pmc.essentials.config.EssentialsConfig
import org.bukkit.Location
import org.bukkit.World
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import java.util.*

class WarpManagerImpl : WarpManager, KoinComponent {

    private val conf by lazy { get<EssentialsConfig>().Warp() }

    override val blacklistedWorlds: Collection<World> = conf.blacklistedWorlds

    override suspend fun get(id: UUID): Warp? {
        TODO("Not yet implemented")
    }

    override suspend fun get(name: String): Warp? {
        TODO("Not yet implemented")
    }

    override suspend fun list(): Collection<Warp> {
        TODO("Not yet implemented")
    }

    override suspend fun has(name: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun create(name: String, location: Location, alias: String?): Warp {
        TODO("Not yet implemented")
    }

    override suspend fun remove(name: String) {
        TODO("Not yet implemented")
    }

    override fun isBlacklisted(world: World): Boolean {
        TODO("Not yet implemented")
    }

}