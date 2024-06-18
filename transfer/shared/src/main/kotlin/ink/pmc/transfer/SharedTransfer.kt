package ink.pmc.transfer

import com.electronwill.nightconfig.core.file.FileConfig
import java.io.File
import java.util.logging.Logger

var disabled = false
lateinit var transferService: AbstractTransferService
lateinit var serverLogger: Logger
lateinit var dataDir: File
lateinit var fileConfig: FileConfig

fun File.loadConfig() {
    fileConfig =  FileConfig.builder(this)
        .async()
        .autoreload()
        .build()
    fileConfig.load()
}