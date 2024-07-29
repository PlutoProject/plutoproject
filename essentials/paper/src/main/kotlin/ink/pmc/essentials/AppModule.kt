package ink.pmc.essentials

import ink.pmc.essentials.api.IEssentials
import ink.pmc.essentials.api.teleport.TeleportManager
import ink.pmc.essentials.api.teleport.random.RandomTeleportManager
import ink.pmc.essentials.config.EssentialsConfig
import ink.pmc.essentials.teleport.TeleportManagerImpl
import ink.pmc.essentials.teleport.random.RandomTeleportManagerImpl
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
    single<RandomTeleportManager> {
        val conf = get<EssentialsConfig>()
        if (conf.RandomTeleport().enabled) {
            RandomTeleportManagerImpl()
        } else {
            throw IllegalStateException("RandomTeleportManager not available")
        }
    }
}