package ink.pmc.common.server

import com.electronwill.nightconfig.core.file.FileConfig
import java.io.File

var disabled = true
lateinit var serverService: ServerService
lateinit var dataDir: File
lateinit var configFile: File
lateinit var config: FileConfig

fun createDataDir() {
    if (!dataDir.exists()) {
        dataDir.mkdirs()
    }
}

fun loadConfig() {
    config = FileConfig.builder(configFile).sync().build()
    config.load()
}