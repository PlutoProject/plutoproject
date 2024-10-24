package ink.pmc.framework

import java.io.File
import java.util.logging.Logger

interface ModuleEntry<T> {
    val server: T
    val dataFolder: File
    val logger: Logger

    suspend fun onLoad()

    suspend fun onEnable()

    suspend fun onDisable()
}