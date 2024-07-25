package ink.pmc.essentials

import org.koin.dsl.module

val appModule = module {
    single { EssentialsConfig(fileConfig) }
}