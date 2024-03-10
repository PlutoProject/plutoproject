package ink.pmc.common.misc.api.sit

import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import java.util.*

interface SitManager {

    val sitters: Map<UUID, Location>

    fun sit(player: Player, location: Location)

    fun isSitting(player: Player): Boolean

    fun stand(player: Player)

    fun getSeat(player: Player): Entity?

    fun getSitLocation(player: Player): Location?

    fun standAll()

}