package ink.pmc.essentials.listeners

import com.destroystokyo.paper.event.player.PlayerAdvancementCriterionGrantEvent
import ink.pmc.essentials.config.DemoWorldOptions
import ink.pmc.essentials.config.EssentialsConfig
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.player.*
import org.bukkit.event.weather.WeatherChangeEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

@Suppress("UNUSED")
object DemoWorldListener : Listener, KoinComponent {
    private val config by lazy { get<EssentialsConfig>().demoWorld }

    private val World.demoWorldOptions: DemoWorldOptions?
        get() = config.worlds[name]

    private val World.demoWorldSpawnpoint: Location?
        get() = demoWorldOptions?.let {
            Location(
                this,
                it.spawnpoint.x,
                it.spawnpoint.y,
                it.spawnpoint.z,
                it.spawnpoint.yaw,
                it.spawnpoint.pitch
            )
        }

    @EventHandler
    fun PlayerJoinEvent.e() {
        player.world.demoWorldOptions?.let {
            player.world.demoWorldSpawnpoint?.let { location -> player.teleport(location) }
        }
    }

    @EventHandler
    fun PlayerMoveEvent.e() {
        player.world.demoWorldOptions?.let {
            if (to.toBlockLocation().y > it.teleportHeight) return
            player.world.demoWorldSpawnpoint?.let { location -> player.teleport(location) }
        }
    }

    @EventHandler
    fun EntityDamageEvent.e() {
        if (entity !is Player) return
        entity.world.demoWorldOptions?.let {
            isCancelled = true
        }
    }

    @EventHandler
    fun EntityDamageByEntityEvent.e() {
        if (damager !is Player) return
        damager.world.demoWorldOptions?.let {
            isCancelled = true
        }
    }

    @EventHandler
    fun FoodLevelChangeEvent.e() {
        if (entity !is Player) return
        entity.world.demoWorldOptions?.let {
            isCancelled = true
        }
    }

    @EventHandler
    fun PlayerDropItemEvent.e() {
        player.world.demoWorldOptions?.let {
            isCancelled = true
        }
    }

    @EventHandler
    fun PlayerInteractEvent.e() {
        player.world.demoWorldOptions?.let {
            isCancelled = true
        }
    }

    @EventHandler
    fun PlayerAdvancementCriterionGrantEvent.e() {
        player.world.demoWorldOptions?.let {
            isCancelled = true
        }
    }

    @EventHandler
    fun PlayerRecipeDiscoverEvent.e() {
        player.world.demoWorldOptions?.let {
            isCancelled = true
        }
    }

    @EventHandler
    fun EntitySpawnEvent.e() {
        entity.world.demoWorldOptions?.let {
            isCancelled = true
        }
    }

    @EventHandler
    fun WeatherChangeEvent.e() {
        world.demoWorldOptions?.let {
            isCancelled = true
        }
    }
}