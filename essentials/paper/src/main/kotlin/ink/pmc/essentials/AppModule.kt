package ink.pmc.essentials

import ink.pmc.essentials.api.IEssentials
import ink.pmc.essentials.api.teleport.TeleportManager
import ink.pmc.essentials.api.teleport.random.RandomTeleportManager
import ink.pmc.essentials.config.EssentialsConfig
import ink.pmc.essentials.repositories.HomeRepository
import ink.pmc.essentials.teleport.TeleportManagerImpl
import ink.pmc.essentials.teleport.random.RandomTeleportManagerImpl
import org.koin.dsl.module

val appModule = module {
    single { EssentialsConfig(fileConfig) }
    single<IEssentials> { EssentialsImpl() }
    single<HomeRepository> { HomeRepository() }
    single<TeleportManager> {
        require(get<EssentialsConfig>().Teleport().enabled) { "TeleportManager not available" }
        TeleportManagerImpl()
    }
    single<RandomTeleportManager> {
        require(get<EssentialsConfig>().RandomTeleport().enabled) { "RandomTeleportManager not available" }
        RandomTeleportManagerImpl()
    }
}