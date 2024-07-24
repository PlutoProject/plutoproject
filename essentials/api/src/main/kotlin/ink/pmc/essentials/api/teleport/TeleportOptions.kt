package ink.pmc.essentials.api.teleport

import org.bukkit.Material

data class TeleportOptions(
    val avoidVoid: Boolean,
    val safeLocationSearchRadius: Int,
    val chunkPrepareRadius: Int,
    val blacklistedBlocks: Set<Material>
)