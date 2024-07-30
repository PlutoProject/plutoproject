package ink.pmc.essentials

import ink.pmc.essentials.api.IEssentials
import ink.pmc.essentials.api.teleport.TeleportManager
import ink.pmc.essentials.api.teleport.random.RandomTeleportManager
import ink.pmc.essentials.config.EssentialsConfig
import ink.pmc.essentials.repositories.HomeRepository
import ink.pmc.essentials.teleport.TeleportManagerImpl
import ink.pmc.essentials.teleport.random.RandomTeleportManagerImpl
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.getKoin

private val conf by lazy { getKoin().get<EssentialsConfig>() }

val appModule = module {
    single { EssentialsConfig(fileConfig) }
    single<IEssentials> { EssentialsImpl() }
    single<HomeRepository> { HomeRepository() }
    single<TeleportManager> {
        require(conf.Teleport().enabled) { "TeleportManager not available" }
        TeleportManagerImpl()
    }
    single<RandomTeleportManager> {
        require(conf.Teleport().enabled && conf.RandomTeleport().enabled) { "RandomTeleportManager not available" }
        RandomTeleportManagerImpl()
    }
}