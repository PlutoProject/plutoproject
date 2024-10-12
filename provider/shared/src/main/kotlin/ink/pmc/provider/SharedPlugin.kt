package ink.pmc.provider

import com.electronwill.nightconfig.core.Config
import com.electronwill.nightconfig.core.file.FileConfig
import java.io.File

fun File.loadConfig(): Config {
    return FileConfig.builder(this)
        .async()
        .autoreload()
        .build()
        .apply { load() }
}