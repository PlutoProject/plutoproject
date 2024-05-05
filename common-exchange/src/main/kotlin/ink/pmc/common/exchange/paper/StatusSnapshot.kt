package ink.pmc.common.exchange.paper

import ink.pmc.common.utils.platform.threadSafeTeleport
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.PlayerInventory
import org.bukkit.potion.PotionEffect

@Suppress("UNUSED")
data class StatusSnapshot(
    val inventory: Inventory,
    val potionEffects: List<PotionEffect>,
    val gameMode: GameMode,
    val location: Location,
    val allowFlight: Boolean,
    val isFlying: Boolean,
    val isInvisible: Boolean
) {

    companion object {
        fun create(player: Player): StatusSnapshot {
            val filteredEffects = player.activePotionEffects.filterNotNull()
            return StatusSnapshot(
                convertToCustomInventory(player.inventory),
                filteredEffects,
                player.gameMode,
                player.location,
                player.allowFlight,
                player.isFlying,
                player.isInvisible
            )
        }

        private fun convertToCustomInventory(playerInventory: PlayerInventory): Inventory {
            return Bukkit.createInventory(null, 45).apply {
                for (i in 0..<41) {
                    setItem(i, playerInventory.getItem(i))
                }
            }
        }
    }

    fun restore(player: Player, restoreLocation: Boolean = true) {
        restoreInventory(player, inventory)
        player.addPotionEffects(potionEffects)
        player.gameMode = gameMode

        if (restoreLocation) {
            player.threadSafeTeleport(location)
        }

        player.allowFlight = allowFlight
        player.isFlying = isFlying
        player.isInvisible = isInvisible
    }

    private fun restoreInventory(player: Player, inventory: Inventory) {
        for (i in 0..<41) {
            player.inventory.setItem(i, inventory.getItem(i))
        }
    }

}