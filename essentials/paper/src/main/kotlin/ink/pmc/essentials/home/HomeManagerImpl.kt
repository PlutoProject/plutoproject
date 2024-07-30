package ink.pmc.essentials.home

import ink.pmc.essentials.api.home.Home
import ink.pmc.essentials.api.home.HomeManager
import ink.pmc.essentials.api.teleport.TeleportManager
import ink.pmc.essentials.config.EssentialsConfig
import ink.pmc.essentials.dtos.HomeDto
import ink.pmc.essentials.repositories.HomeRepository
import ink.pmc.utils.storage.entity.dto
import org.bson.types.ObjectId
import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.bukkit.World
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import java.util.*

class HomeManagerImpl : HomeManager, KoinComponent {

    private val conf by lazy { get<EssentialsConfig>().Home() }
    private val repo by inject<HomeRepository>()

    override val maxHomes: Int = conf.maxHomes
    override val blacklistedWorlds: Collection<World> = conf.blacklistedWorlds

    override suspend fun get(id: UUID): Home? {
        val dto = repo.findById(id) ?: return null
        return HomeImpl(dto)
    }

    override suspend fun list(player: OfflinePlayer): Collection<Home> {
        val dto = repo.findByPlayer(player)
        return dto.map { HomeImpl(it) }
    }

    override suspend fun has(player: OfflinePlayer, name: String): Boolean {
        return repo.hasByName(player, name)
    }

    override suspend fun remove(id: UUID) {
        repo.deleteById(id)
    }

    override suspend fun remove(player: OfflinePlayer, name: String) {
        repo.deleteByName(player, name)
    }

    override suspend fun create(owner: Player, name: String, location: Location): Home {
        val dto = HomeDto(
            ObjectId(),
            UUID.randomUUID(),
            name,
            System.currentTimeMillis(),
            location.dto,
            owner.uniqueId
        )
        val home = HomeImpl(dto)
        repo.save(dto)
        return home
    }

    override suspend fun update(home: Home) {
        home.update()
    }

    override fun isBlacklisted(world: World): Boolean {
        return blacklistedWorlds.contains(world)
    }

}