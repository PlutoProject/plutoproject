package ink.pmc.provider

import org.koin.dsl.module

val commonModule = module {
    single<ProviderService> { ProviderServiceImpl() }
}