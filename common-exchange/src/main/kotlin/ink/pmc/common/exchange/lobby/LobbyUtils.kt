package ink.pmc.common.exchange.lobby

import ink.pmc.common.exchange.fileConfig
import ink.pmc.common.exchange.world
import org.bukkit.Location

val spawn: Location
    get() = Location(
        world,
        fileConfig.get("lobby-settings.spawn-location.x"),
        fileConfig.get("lobby-settings.spawn-location.y"),
        fileConfig.get("lobby-settings.spawn-location.z"),
        fileConfig.get("lobby-settings.spawn-location.yaw"),
        fileConfig.get("lobby-settings.spawn-location.pitch"),
    )