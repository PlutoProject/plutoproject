package ink.pmc.common.exchange.paper

import ink.pmc.common.utils.platform.threadSafeTeleport
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect

@Suppress("UNUSED")
data class StatusSnapshot(
    val inventory: Map<Int, ItemStack?>,
    val potionEffects: List<PotionEffect>,
    val gameMode: GameMode,
    val location: Location
) {

    companion object {
        fun create(player: Player): StatusSnapshot {
            val contents = mutableMapOf<Int, ItemStack?>()

            for (i in 0..40) {
                contents[i] = player.inventory.getItem(i)
            }

            val mappedEffects = player.activePotionEffects.filterNotNull()
            return StatusSnapshot(contents, mappedEffects, player.gameMode, player.location)
        }
    }

    fun restore(player: Player, restoreLocation: Boolean = true) {
        for (i in 0..40) {
            player.inventory.setItem(i, inventory[i])
        }

        player.clearActivePotionEffects()
        player.addPotionEffects(potionEffects)
        player.gameMode = gameMode

        if (restoreLocation) {
            player.threadSafeTeleport(location)
        }
    }

}