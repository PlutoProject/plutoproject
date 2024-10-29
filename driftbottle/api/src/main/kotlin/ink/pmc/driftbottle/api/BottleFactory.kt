package ink.pmc.driftbottle.api

import ink.pmc.framework.utils.inject.inlinedGet
import org.bukkit.entity.Player

interface BottleFactory {
    companion object : BottleFactory by inlinedGet()

    fun create(creator: Player, content: List<String>): Bottle
}