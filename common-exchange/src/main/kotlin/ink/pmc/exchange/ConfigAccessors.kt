package ink.pmc.exchange

import org.bukkit.Location

val lobbyServerName: String
    get() = fileConfig.get("lobby-server")

val serverName: String
    get() = fileConfig.get("server-name")

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