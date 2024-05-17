package ink.pmc.common.exchange

val dailyTickets: Long
    get() = fileConfig.get("daily-tickets")

val lobbyServerName: String
    get() = fileConfig.get("lobby-server")