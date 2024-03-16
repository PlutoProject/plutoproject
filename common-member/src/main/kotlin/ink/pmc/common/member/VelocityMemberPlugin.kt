package ink.pmc.common.member

import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent
import com.velocitypowered.api.plugin.Dependency
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.logging.Logger

lateinit var proxyServer: ProxyServer
lateinit var proxyLogger: Logger

fun saveDefaultConfig(output: File) {
    val input: InputStream = Thread.currentThread().contextClassLoader.getResourceAsStream("config.yml")
        ?: throw IllegalArgumentException("Resource not found")
    Files.copy(input, output.toPath(), StandardCopyOption.REPLACE_EXISTING)
    input.close()
}

@Plugin(
    id = "common-member",
    name = "common-member",
    version = "1.0.0",
    dependencies = [Dependency(id = "common-dependency-loader-velocity")]
)
@Suppress("UNUSED", "UNUSED_PARAMETER")
class MemberPluginVelocity {

    @Inject
    fun memberPluginVelocity(server: ProxyServer, logger: Logger, @DataDirectory dataDirectory: Path) {
        proxyServer = server
        proxyLogger = logger

        dataDir = dataDirectory.toFile()
        configFile = File(dataDir, "config.yml")

        if (!configFile.exists()) {
            saveDefaultConfig(configFile)
        }

        initMemberManager()
    }

    @Subscribe
    fun proxyShutdownEvent(event: ProxyShutdownEvent) {
        mongoClient.close()
    }

}