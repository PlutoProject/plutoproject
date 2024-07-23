package ink.pmc.essentials.api.warp

import org.bukkit.Location
import org.bukkit.entity.Player
import java.math.BigDecimal
import java.time.Instant

@Suppress("UNUSED")
interface Warp {

    val name: String
    var alias: String?
    val createdAt: Instant
    var location: Location
    var cost: BigDecimal

    fun teleport(player: Player, cost: Boolean = true, prompt: Boolean = true)

    suspend fun teleportSuspend(player: Player, cost: Boolean = true, prompt: Boolean = true)

    suspend fun save()

}