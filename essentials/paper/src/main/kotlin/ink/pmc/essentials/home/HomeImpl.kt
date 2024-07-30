package ink.pmc.essentials.home

import ink.pmc.essentials.api.home.Home
import ink.pmc.essentials.api.teleport.TeleportManager
import ink.pmc.essentials.dtos.HomeDto
import ink.pmc.essentials.repositories.HomeRepository
import ink.pmc.utils.concurrent.submitAsync
import ink.pmc.utils.storage.entity.dto
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.Instant
import java.util.*

class HomeImpl(private val dto: HomeDto) : Home, KoinComponent {

    private val repo by inject<HomeRepository>()
    private val teleport by inject<TeleportManager>()

    override val id: UUID = dto.id
    override var name: String = dto.name
    override val createdAt: Instant = Instant.ofEpochMilli(dto.createdAt)
    override var location: Location =
        requireNotNull(dto.location.location) { loadFailed("cannot to obtain location ${dto.location}") }
    override val owner: OfflinePlayer =
        requireNotNull(Bukkit.getOfflinePlayer(dto.owner)) { loadFailed("cannot obtain OfflinePlayer ${dto.owner}") }

    private fun loadFailed(reason: String): String {
        return "Failed to load Home $id: $reason"
    }

    override fun teleport(player: Player, prompt: Boolean) {
        submitAsync {
            teleportSuspend(player, prompt)
        }
    }

    override suspend fun teleportSuspend(player: Player, prompt: Boolean) {
        teleport.teleportSuspend(player, location, prompt = prompt)
    }

    private fun toDto(): HomeDto {
        return dto.copy(
            id = id,
            name = name,
            createdAt = createdAt.toEpochMilli(),
            location = location.dto,
            owner = owner.uniqueId
        )
    }

    override suspend fun update() {
        repo.update(toDto())
    }

}