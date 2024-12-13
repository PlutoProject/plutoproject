package ink.pmc.serverselector.listener

import com.destroystokyo.paper.event.player.PlayerAdvancementCriterionGrantEvent
import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent
import ink.pmc.framework.interactive.GuiManager
import ink.pmc.serverselector.*
import ink.pmc.serverselector.screen.ServerSelectorScreen
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.*
import org.bukkit.event.player.*
import org.bukkit.event.weather.WeatherChangeEvent

@Suppress("UNUSED")
object PlayerListener : Listener {
    @EventHandler
    fun PlayerJoinEvent.e() {
        if (!player.hasPermission(PROTECTION_BYPASS)) {
            player.inventory.clear()
        }
        if (!player.inventory.contents
                .filterNotNull()
                .any { it.isServerSelector }
        ) {
            player.inventory.addItem(ServerSelectorItem)
        }
        player.teleportAsync(lobbyWorldSpawn)
        player.health = player.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value ?: error("Unexpected")
        player.foodLevel = 20
        player.saturation = 20f
        player.clearActivePotionEffects()
        player.setRespawnLocation(lobbyWorldSpawn, true)
        if (player.hasPermission(PROTECTION_BYPASS)) return
        player.gameMode = GameMode.SURVIVAL
    }

    @EventHandler
    fun PlayerInteractEvent.e() {
        if (action.isRightClick && item?.isServerSelector == true) {
            GuiManager.startScreen(player, ServerSelectorScreen())
            isCancelled = true
            return
        }
        if (action == Action.PHYSICAL && clickedBlock?.type == Material.FARMLAND) {
            isCancelled = true
            return
        }
        if (player.hasPermission(PROTECTION_BYPASS)) return
        isCancelled = true
    }

    @EventHandler
    fun PlayerDropItemEvent.e() {
        if (player.hasPermission(PROTECTION_BYPASS)) return
        isCancelled = true
    }

    @EventHandler
    fun EntityPickupItemEvent.e() {
        if (entity !is Player) return
        if (entity.hasPermission(PROTECTION_BYPASS)) return
        isCancelled = true
    }

    @EventHandler
    fun PlayerPickupArrowEvent.e() {
        if (player.hasPermission(PROTECTION_BYPASS)) return
        isCancelled = true
    }

    @EventHandler
    fun PlayerPickupExperienceEvent.e() {
        if (player.hasPermission(PROTECTION_BYPASS)) return
        isCancelled = true
    }

    @EventHandler
    fun EntityDamageEvent.e() {
        if (entity !is Player) return
        isCancelled = true
    }

    @EventHandler
    fun EntityDamageByEntityEvent.e() {
        if (damager !is Player) return
        if (damager.hasPermission(PROTECTION_BYPASS)) return
        isCancelled = true
    }

    @EventHandler
    fun FoodLevelChangeEvent.e() {
        if (entity !is Player) return
        isCancelled = true
    }

    @EventHandler
    fun PlayerAdvancementCriterionGrantEvent.e() {
        isCancelled = true
    }

    @EventHandler
    fun PlayerRecipeDiscoverEvent.e() {
        isCancelled = true
    }

    @EventHandler
    fun PlayerMoveEvent.e() {
        val world = player.world
        if (world != lobbyWorld) return
        if (player.location.blockY in world.minHeight..world.maxHeight) return
        player.teleportAsync(lobbyWorldSpawn)
    }

    @EventHandler
    fun PlayerChangedWorldEvent.e() {
        if (player.world == lobbyWorld) return
        player.resetPlayerTime()
    }

    @EventHandler
    fun EntitySpawnEvent.e() {
        if (entity.world != lobbyWorld) return
        isCancelled = true
    }

    @EventHandler
    fun WeatherChangeEvent.e() {
        if (world != lobbyWorld) return
        isCancelled = true
    }
}