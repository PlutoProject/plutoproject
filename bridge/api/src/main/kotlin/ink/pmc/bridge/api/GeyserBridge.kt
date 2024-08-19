package ink.pmc.bridge.api

import ink.pmc.utils.inject.inlinedGet
import org.bukkit.entity.Player

interface GeyserBridge {

    companion object : GeyserBridge by inlinedGet()

    suspend fun closeForm(player: Player)

    fun closeFormAsync(player: Player)

}