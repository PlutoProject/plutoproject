package ink.pmc.provider

import com.electronwill.nightconfig.core.file.FileConfig
import java.io.File

lateinit var providerService: IProviderService
lateinit var fileConfig: FileConfig

fun File.loadProviderService() {
    fileConfig =  FileConfig.builder(this)
        .async()
        .autoreload()
        .build()
    fileConfig.load()
    providerService = ProviderServiceImpl(fileConfig)
    IProviderService.instance = providerService
}