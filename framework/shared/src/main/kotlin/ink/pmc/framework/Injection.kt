package ink.pmc.framework

import com.sksamuel.hoplite.PropertySource
import ink.pmc.framework.options.repositories.OptionsContainerRepository
import ink.pmc.framework.provider.ProviderImpl
import ink.pmc.framework.rpc.RpcClientImpl
import ink.pmc.framework.rpc.RpcServerImpl
import ink.pmc.framework.options.OptionDescriptorFactoryImpl
import ink.pmc.framework.options.OptionsManagerImpl
import ink.pmc.options.api.OptionsManager
import ink.pmc.options.api.factory.OptionDescriptorFactory
import ink.pmc.provider.Provider
import ink.pmc.rpc.api.RpcClient
import ink.pmc.rpc.api.RpcServer
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
    single<RpcClient> { RpcClientImpl() }
    single<RpcServer> { RpcServerImpl() }
    single<OptionsContainerRepository> { OptionsContainerRepository(Provider.defaultMongoDatabase.getCollection("options_data")) }
    single<OptionsManager> { OptionsManagerImpl() }
    single<OptionDescriptorFactory> { OptionDescriptorFactoryImpl() }
}