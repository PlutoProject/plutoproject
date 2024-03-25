package ink.pmc.common.server

import com.electronwill.nightconfig.core.file.FileConfig
import ink.pmc.common.server.impl.NettyServerService
import java.io.File
import java.util.logging.Logger

var disabled = true
lateinit var serverService: NettyServerService
lateinit var pluginLogger: Logger
lateinit var dataDir: File
lateinit var configFile: File
lateinit var config: FileConfig

val token: String
    get() = config.get("token")
val address: String
    get() = config.get("address")
val host: String
    get() {
        val index = address.indexOf(':')

        if (address.indexOf(':') == -1) {
            return address
        }

        return address.substring(0, index)
    }
val port: Int
    get() {
        val index = address.indexOf(':')

        if (address.indexOf(':') == -1) {
            return 5677
        }

        return address.substring(index + 1, address.length).toInt()
    }
val id: Long
    get() = config.getLong("configuration.id")
val name: String
    get() = config.get("configuration.name")

fun createDataDir() {
    if (!dataDir.exists()) {
        dataDir.mkdirs()
    }
}

fun loadConfig() {
    config = FileConfig.builder(configFile).sync().build()
    config.load()
}