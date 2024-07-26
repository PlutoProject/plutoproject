package ink.pmc.essentials

import ink.pmc.essentials.api.player.PlayerManager
import ink.pmc.essentials.manager.PlayerManagerImpl
import org.koin.dsl.module

val appModule = module {
    single { EssentialsConfig(fileConfig) }
    single<PlayerManager> { PlayerManagerImpl() }
}