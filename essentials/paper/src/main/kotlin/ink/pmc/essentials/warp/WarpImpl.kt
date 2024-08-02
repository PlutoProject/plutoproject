package ink.pmc.essentials.warp

import ink.pmc.essentials.api.warp.Warp
import org.bukkit.Location
import org.bukkit.entity.Player
import java.time.Instant
import java.util.*

class WarpImpl : Warp {

    override val id: UUID
        get() = TODO("Not yet implemented")
    override val name: String
        get() = TODO("Not yet implemented")
    override var alias: String?
        get() = TODO("Not yet implemented")
        set(value) {}
    override val createdAt: Instant
        get() = TODO("Not yet implemented")
    override var location: Location
        get() = TODO("Not yet implemented")
        set(value) {}

    override fun teleport(player: Player, cost: Boolean, prompt: Boolean) {
        TODO("Not yet implemented")
    }

    override suspend fun teleportSuspend(player: Player, cost: Boolean, prompt: Boolean) {
        TODO("Not yet implemented")
    }

    override suspend fun save() {
        TODO("Not yet implemented")
    }

}