package ink.pmc.hypervisor

import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.EntityType
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityChangeBlockEvent

fun handleEndermanPlaceBlock(event: EntityChangeBlockEvent) {
    val entity = event.entity

    if (entity.type != EntityType.ENDERMAN) {
        return
    }

    if (!isEndBiome(event.block)) {
        return
    }

    val to = event.to

    if (to != Material.RED_MUSHROOM && to != Material.BROWN_MUSHROOM) {
        return
    }

    event.isCancelled = true
}

fun handlePlayerPlaceBlock(event: BlockPlaceEvent) {
    val block = event.block
    val player = event.player

    if (!isEndBiome(block)) {
        return
    }

    if (block.type != Material.RED_MUSHROOM && block.type != Material.BROWN_MUSHROOM) {
        return
    }

    event.isCancelled = true

    player.showTitle(MUSHROOM_DETECTOR_PLAYER_PLACE)
    player.playSound(MUSHROOM_DETECTOR_PLAYER_PLACE_SOUND)
}

private fun isEndBiome(block: Block): Boolean {
    val biome = block.biome
    return biome.toString().lowercase().contains("end")
}