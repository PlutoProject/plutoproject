package ink.pmc.common.exchange

import com.electronwill.nightconfig.core.file.FileConfig
import ink.pmc.common.member.api.Member
import java.io.File
import java.util.logging.Logger

var disabled = true
lateinit var serverLogger: Logger
lateinit var exchangeService: AbstractExchangeService
lateinit var dataDir: File
lateinit var configFile: File
lateinit var config: FileConfig

fun createDataDir() {
    if (!dataDir.exists()) {
        dataDir.mkdirs()
    }
}

fun loadConfig(file: File) {
    config = FileConfig.builder(file).sync().build()
    config.load()
    ExchangeConfig.identity = config.get("server-identity")
    ExchangeConfig.ExchangeLobby.worldName = config.get("exchange-lobby.world-name")
    ExchangeConfig.ExchangeLobby.TeleportLocation.x = config.get("exchange-lobby.teleport-location.x")
    ExchangeConfig.ExchangeLobby.TeleportLocation.y = config.get("exchange-lobby.teleport-location.y")
    ExchangeConfig.ExchangeLobby.TeleportLocation.z = config.get("exchange-lobby.teleport-location.z")
    ExchangeConfig.ExchangeLobby.TeleportLocation.yaw = config.get("exchange-lobby.teleport-location.yaw")
    ExchangeConfig.ExchangeLobby.TeleportLocation.pitch = config.get("exchange-lobby.teleport-location.pitch")
}

suspend fun initExchangeData(member: Member) {
    val data = member.dataContainer

    if (data.contains("exchange")) {
        return
    }

    data["exchange.tickets"] = 0L
    member.update()
}