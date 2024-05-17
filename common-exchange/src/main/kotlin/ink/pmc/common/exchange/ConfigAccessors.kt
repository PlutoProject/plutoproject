package ink.pmc.common.exchange

import org.bukkit.Location

val dailyTickets: Long
    get() = fileConfig.get("daily-tickets")

val lobbyServerName: String
    get() = fileConfig.get("lobby-server")

val serverName: String
    get() = fileConfig.get("server-name")

val lobbyWorldName: String
    get() = fileConfig.get<String>("lobby-settings.world")

val lobbySpawnLocation: Location
    get() = Location(
        world,
        fileConfig.get("lobby-settings.spawn-location.x"),
        fileConfig.get("lobby-settings.spawn-location.y"),
        fileConfig.get("lobby-settings.spawn-location.z"),
        fileConfig.get("lobby-settings.spawn-location.yaw"),
        fileConfig.get("lobby-settings.spawn-location.pitch"),
    )