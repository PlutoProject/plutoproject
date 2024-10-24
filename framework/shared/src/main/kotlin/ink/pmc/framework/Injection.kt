package ink.pmc.framework

import com.sksamuel.hoplite.PropertySource
import ink.pmc.framework.provider.ProviderImpl
import ink.pmc.provider.Provider
import ink.pmc.utils.config.preconfiguredConfigLoaderBuilder
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.io.File

val FRAMEWORK_CONFIG = named("framework_config")

val commonModule = module {
    single<FrameworkConfig> {
        preconfiguredConfigLoaderBuilder()
            .addPropertySource(PropertySource.file(get<File>(FRAMEWORK_CONFIG)))
            .build()
            .loadConfigOrThrow()
    }
    single<Provider> { ProviderImpl() }
}