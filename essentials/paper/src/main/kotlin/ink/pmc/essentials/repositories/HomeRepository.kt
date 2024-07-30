package ink.pmc.essentials.repositories

import ink.pmc.essentials.config.EssentialsConfig
import ink.pmc.essentials.dtos.HomeDto
import ink.pmc.provider.ProviderService
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.UUID

class HomeRepository : KoinComponent {

    private val conf by inject<EssentialsConfig>()
    private val db =
        ProviderService.defaultMongoDatabase.getCollection<HomeDto>("ess_${conf.serverName}_homes")

    suspend fun findById(id: UUID): HomeDto? {
        TODO()
    }

    suspend fun findByName(player: Player, name: String): HomeDto? {
        TODO()
    }

    suspend fun save(dto: HomeDto) {

    }

    suspend fun update(dto: HomeDto) {

    }

}