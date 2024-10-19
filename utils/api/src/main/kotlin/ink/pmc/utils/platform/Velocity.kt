package ink.pmc.utils.platform

import com.velocitypowered.api.plugin.PluginContainer
import com.velocitypowered.api.proxy.ProxyServer
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.StandardCopyOption

lateinit var proxyThread: Thread
lateinit var proxy: ProxyServer
lateinit var velocityUtilsPlugin: PluginContainer

fun saveDefaultConfig(clazz: Class<*>, output: File) {
    saveConfig(clazz, "config.toml", output)
}

fun saveConfig(clazz: Class<*>, name: String, output: File) {
    val input: InputStream = clazz.getResourceAsStream("/$name")
        ?: throw IllegalArgumentException("Resource not found")
    Files.copy(input, output.toPath(), StandardCopyOption.REPLACE_EXISTING)
    input.close()
}