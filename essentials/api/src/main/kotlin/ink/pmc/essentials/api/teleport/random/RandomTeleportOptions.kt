package ink.pmc.essentials.api.teleport.random

import ink.pmc.utils.world.Loc2D
import org.bukkit.Material
import org.bukkit.block.Biome
import java.math.BigDecimal
import kotlin.time.Duration

data class RandomTeleportOptions(
    val center: Loc2D,
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