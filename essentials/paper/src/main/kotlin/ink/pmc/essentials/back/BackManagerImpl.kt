package ink.pmc.essentials.back

import ink.pmc.essentials.api.back.BackManager
import ink.pmc.essentials.api.teleport.TeleportManager
import ink.pmc.essentials.config.EssentialsConfig
import ink.pmc.utils.concurrent.submitAsync
import org.bukkit.Location
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import java.util.concurrent.ConcurrentHashMap

class BackManagerImpl : BackManager, KoinComponent {

    private val conf by lazy { get<EssentialsConfig>().Back() }
    private val teleport by inject<TeleportManager>()

    override val maxLocations: Int = conf.maxLocations
    override val previousLocations: MutableMap<Player, Location> = ConcurrentHashMap()

    override fun has(player: Player): Boolean {
        return previousLocations.containsKey(player)
    }

    override fun get(player: Player): Location? {
        return previousLocations[player]
    }

    override fun back(player: Player) {
        submitAsync {
            backSuspend(player)
        }
    }

    override suspend fun backSuspend(player: Player) {
        val loc = requireNotNull(get(player)) { "Player ${player.name} doesn't have a back location" }
        store(player, player.location)
        val opt = teleport.getWorldTeleportOptions(loc.world).copy(bypassSafeCheck = true)
        teleport.teleportSuspend(player, loc, opt)
    }

    override suspend fun store(player: Player, location: Location) {
        val loc = if (!teleport.isSafe(location)) {
            teleport.searchSafeLocationSuspend(location) ?: location
        } else {
            location
        }
        previousLocations[player] = loc
    }

    override fun discard(player: Player) {
        previousLocations.remove(player)
    }

}