package ink.pmc.utils.platform

import com.velocitypowered.api.proxy.server.RegisteredServer
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import kotlin.jvm.optionals.getOrNull

fun saveDefaultConfig(clazz: Class<*>, output: File) {
    saveConfig(clazz, "config.toml", output)
}

fun saveConfig(clazz: Class<*>, name: String, output: File) {
    val input: InputStream = clazz.getResourceAsStream("/$name")
        ?: throw IllegalArgumentException("Resource not found")
    Files.copy(input, output.toPath(), StandardCopyOption.REPLACE_EXISTING)
    input.close()
}

val String.namedServer: RegisteredServer?
    get() = proxy.getServer(this).getOrNull()