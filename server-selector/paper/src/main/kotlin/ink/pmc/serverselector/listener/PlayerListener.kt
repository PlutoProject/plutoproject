package ink.pmc.serverselector.listener

import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent
import ink.pmc.serverselector.PROTECTION_BYPASS
import ink.pmc.serverselector.ServerSelectorItem
import ink.pmc.serverselector.isServerSelector
import org.bukkit.GameMode
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.player.*

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
        player.health = player.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value ?: error("Unexpected")
        player.foodLevel = 20
        player.saturation = 20f
        // TODO: 设置重生点到大厅
        // TODO: 传送到大厅
        if (player.hasPermission(PROTECTION_BYPASS)) return
        player.gameMode = GameMode.SURVIVAL
    }

    @EventHandler
    fun PlayerInteractEvent.e() {
        if (action.isRightClick && item?.isServerSelector == true) {
            // TODO: 打开菜单
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
    fun PlayerMoveEvent.e() {
        // TODO: 检测是否在大厅
        if (player.height < player.world.minHeight) {
            // TODO: 传送到大厅
        }
    }
}