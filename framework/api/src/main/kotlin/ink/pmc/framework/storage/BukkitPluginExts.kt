package ink.pmc.framework.storage

import org.bukkit.plugin.Plugin
import java.io.File

fun Plugin.saveResourceIfNotExisted(path: String): File {
    val file = File(dataFolder, path)
    if (!file.exists()) {
        saveResource(path, false)
    }
    return file
}