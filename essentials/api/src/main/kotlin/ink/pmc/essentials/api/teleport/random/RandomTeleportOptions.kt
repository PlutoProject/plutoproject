package ink.pmc.essentials.api.teleport.random

import ink.pmc.utils.world.Pos2D
import org.bukkit.block.Biome
import java.math.BigDecimal
import kotlin.time.Duration

data class RandomTeleportOptions(
    val center: Pos2D,
    val spawnPointAsCenter: Boolean,
    val startRadius: Int,
    val endRadius: Int,
    val maxHeight: Int,
    val minHeight: Int,
    val noCover: Boolean,
    val maxAttempts: Int,
    val cooldown: Duration,
    val cost: BigDecimal,
    val blacklistedBiomes: Set<Biome>,
)