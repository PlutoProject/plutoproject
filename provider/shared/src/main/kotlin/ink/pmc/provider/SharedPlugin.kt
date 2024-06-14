package ink.pmc.provider

import com.electronwill.nightconfig.core.file.FileConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

lateinit var providerService: IProviderService
lateinit var fileConfig: FileConfig

suspend fun FileConfig.loadProviderService() {
    fileConfig = this

    withContext(Dispatchers.IO) {
        providerService = ProviderServiceImpl(fileConfig)
    }
}