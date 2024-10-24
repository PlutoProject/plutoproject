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

fun saveResourceIfNotExisted(clazz: Class<*>, name: String, output: File): File {
    if (output.exists()) return output
    val input: InputStream = clazz.getResourceAsStream("/$name")
        ?: throw IllegalArgumentException("Resource not found")
    Files.copy(input, output.toPath(), StandardCopyOption.REPLACE_EXISTING)
    input.close()
    return output
}

fun saveDefaultConfig(clazz: Class<*>, folder: File): File {
    return saveResourceIfNotExisted(clazz, "config.conf", File(folder, "config.conf"))
}

inline val velocityDepClassLoader: ClassLoader
    get() = Class.forName("ink.pmc.framework.VelocityPlugin").classLoader