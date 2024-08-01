package ink.pmc.exchange.paper

import ink.pmc.exchange.fileConfig
import ink.pmc.exchange.world
import org.bukkit.Location

val lobbyWorldName: String
    get() = fileConfig.get("lobby-settings.world")

val lobbySpawnLocation: Location
    get() = Location(
        world,
        fileConfig.get("lobby-settings.spawn-location.x"),
        fileConfig.get("lobby-settings.spawn-location.y"),
        fileConfig.get("lobby-settings.spawn-location.z"),
        fileConfig.get("lobby-settings.spawn-location.yaw"),
        fileConfig.get("lobby-settings.spawn-location.pitch"),
    )