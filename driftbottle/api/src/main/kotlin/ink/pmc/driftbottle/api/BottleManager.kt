package ink.pmc.driftbottle.api

import ink.pmc.framework.utils.inject.inlinedGet
import org.bukkit.entity.Player
import java.util.*

interface BottleManager {
    companion object : BottleManager by inlinedGet()

    suspend fun get(id: UUID): Bottle?

    suspend fun getByCreator(player: Player): List<Bottle>

    suspend fun random(): Bottle?

    suspend fun delete(id: UUID)
}