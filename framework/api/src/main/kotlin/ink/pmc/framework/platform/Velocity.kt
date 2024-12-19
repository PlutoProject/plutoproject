package ink.pmc.framework.platform

import com.velocitypowered.api.proxy.ProxyServer
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.StandardCopyOption

lateinit var proxyThread: Thread
lateinit var proxy: ProxyServer

fun saveResourceIfNotExisted(clazz: Class<*>, name: String, output: File): File {
    if (output.exists()) return output
    val input: InputStream = clazz.getResourceAsStream("/$name")
        ?: throw IllegalArgumentException("Resource not found")
    if (!output.exists()) {
        output.parentFile?.mkdirs()
        output.createNewFile()
    }
    Files.copy(input, output.toPath(), StandardCopyOption.REPLACE_EXISTING)
    input.close()
    return output
}

fun saveDefaultConfig(clazz: Class<*>, folder: File): File {
    return saveResourceIfNotExisted(clazz, "config.conf", File(folder, "config.conf"))
}