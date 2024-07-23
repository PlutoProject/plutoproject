package ink.pmc.essentials.api.teleport

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Biome
import java.math.BigDecimal
import kotlin.time.Duration

data class RandomTeleportOption(
    val center: Location,
    val startRadius: Double,
    val endRadius: Double,
    val minHeight: Double,
    val maxHeight: Double,
    val noCover: Boolean,
    val maxRetries: Int,
    val cooldown: Duration,
    val cost: BigDecimal,
    val blacklistedBiomes: Set<Biome>,
    val blacklistedBlocks: Set<Material>
)