package ink.pmc.exchange

val lobbyServerName: String
    get() = fileConfig.get("lobby-server")

val serverName: String
    get() = fileConfig.get("server-name")