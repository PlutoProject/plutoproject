package ink.pmc.rpc

import com.electronwill.nightconfig.core.file.FileConfig
import java.io.File
import java.util.logging.Logger

var disabled = true
lateinit var serverLogger: Logger
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
}