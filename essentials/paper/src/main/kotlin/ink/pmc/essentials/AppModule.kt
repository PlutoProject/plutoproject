package ink.pmc.essentials

import ink.pmc.essentials.api.IEssentials
import ink.pmc.essentials.api.teleport.TeleportManager
import ink.pmc.essentials.manager.TeleportManagerImpl
import org.koin.dsl.module

val appModule = module {
    single { EssentialsConfig(fileConfig) }
    single<IEssentials> {
        EssentialsImpl()
    }
    single<TeleportManager> {
        val conf = get<EssentialsConfig>()
        if (conf.Teleport().enabled) {
            TeleportManagerImpl()
        } else {
            throw IllegalStateException("TeleportManager not available")
        }
    }
}