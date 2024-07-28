package ink.pmc.essentials.api.teleport.random

import org.bukkit.Chunk

data class RandomTeleportCache(
    val center: Chunk,
    val preservedChunks: Collection<Chunk>
)