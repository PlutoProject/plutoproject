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
lateinit var fileConfig: FileConfig

fun createDataDir() {
    if (!dataDir.exists()) {
        dataDir.mkdirs()
    }
}

fun loadConfig(file: File) {
    fileConfig = FileConfig.builder(file).sync().build()
    fileConfig.load()
    ExchangeConfig.identity = fileConfig.get("server-identity")
    ExchangeConfig.Tickets.daily = fileConfig.get("tickets.daily")
}

fun initExchangeData(member: Member) {
    val data = member.dataContainer

    if (data.contains(TICKET_KEY)) {
        return
    }

    data[TICKET_KEY] = 0L
}