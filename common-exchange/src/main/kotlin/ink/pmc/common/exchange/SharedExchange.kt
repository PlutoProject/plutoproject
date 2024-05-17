package ink.pmc.common.exchange

import com.electronwill.nightconfig.core.file.FileConfig
import ink.pmc.common.member.api.Member
import java.io.File
import java.util.logging.Logger

var disabled = true
lateinit var serverLogger: Logger
lateinit var exchangeService: BaseExchangeServiceImpl<*>
lateinit var dataDir: File
lateinit var configFile: File
lateinit var fileConfig: FileConfig

fun createDataDir() {
    if (!dataDir.exists()) {
        dataDir.mkdirs()
    }
}

fun loadConfig(file: File) {
    fileConfig = FileConfig.builder(file)
        .autosave()
        .autoreload()
        .sync()
        .build()
    fileConfig.load()
}