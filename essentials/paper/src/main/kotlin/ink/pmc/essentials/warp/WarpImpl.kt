package ink.pmc.essentials.warp

import ink.pmc.essentials.api.teleport.TeleportManager
import ink.pmc.essentials.api.warp.Warp
import ink.pmc.essentials.api.warp.WarpManager
import ink.pmc.essentials.api.warp.WarpTeleportEvent
import ink.pmc.essentials.dtos.WarpDto
import ink.pmc.essentials.home.loadFailed
import ink.pmc.essentials.repositories.WarpRepository
import ink.pmc.utils.concurrent.async
import ink.pmc.utils.concurrent.submitAsync
import ink.pmc.utils.storage.entity.dto
import org.bukkit.Location
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.Instant
import java.util.*

class WarpImpl(private val dto: WarpDto) : Warp, KoinComponent {

    private val manager by inject<WarpManager>()
    private val repo by inject<WarpRepository>()
    private val teleport by inject<TeleportManager>()

    override val id: UUID = dto.id
    override val name: String = dto.name
    override var alias: String? = dto.alias
    override val createdAt: Instant = Instant.ofEpochMilli(dto.createdAt)
    override var location: Location =
        requireNotNull(dto.location.location) {
            loadFailed(id, "cannot to obtain location ${dto.location}")
        }
    override val isLoaded: Boolean
        get() = manager.isLoaded(id)

    override fun teleport(player: Player, prompt: Boolean) {
        submitAsync {
            teleportSuspend(player, prompt)
        }
    }

    override suspend fun teleportSuspend(player: Player, prompt: Boolean) {
        async {
            val options = teleport.getWorldTeleportOptions(location.world).copy(bypassSafeCheck = true)
            // 必须异步触发
            val event = WarpTeleportEvent(player, player.location, this@WarpImpl).apply { callEvent() }
            if (event.isCancelled) return@async
            teleport.teleportSuspend(player, location, options, prompt)
        }
    }

    private fun toDto(): WarpDto = dto.copy(
        id = id,
        name = name,
        alias = alias,
        createdAt = createdAt.toEpochMilli(),
        location = location.dto,
    )

    override suspend fun update() {
        repo.update(toDto())
    }

}