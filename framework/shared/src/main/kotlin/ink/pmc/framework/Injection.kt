package ink.pmc.framework

import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.sksamuel.hoplite.PropertySource
import ink.pmc.framework.options.OptionDescriptorFactoryImpl
import ink.pmc.framework.options.OptionsManagerImpl
import ink.pmc.framework.options.repositories.OptionsContainerRepository
import ink.pmc.framework.playerdb.DatabaseRepository
import ink.pmc.framework.playerdb.PlayerDbImpl
import ink.pmc.framework.provider.ProviderImpl
import ink.pmc.framework.rpc.RpcClientImpl
import ink.pmc.framework.rpc.RpcServerImpl
import ink.pmc.framework.visual.ToastFactoryImpl
import ink.pmc.options.api.OptionsManager
import ink.pmc.options.api.factory.OptionDescriptorFactory
import ink.pmc.playerdb.api.PlayerDb
import ink.pmc.provider.Provider
import ink.pmc.rpc.api.RpcClient
import ink.pmc.rpc.api.RpcServer
import ink.pmc.utils.config.preconfiguredConfigLoaderBuilder
import ink.pmc.visual.api.toast.ToastFactory
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.io.File

val FRAMEWORK_CONFIG = named("framework_config")

private inline fun <reified T : Any> getCollection(name: String): MongoCollection<T> {
    return Provider.defaultMongoDatabase.getCollection(name)
}

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
    single<DatabaseRepository> { DatabaseRepository(getCollection("player_database_data")) }
    single<PlayerDb> { PlayerDbImpl() }
    single<OptionsContainerRepository> { OptionsContainerRepository(getCollection("options_data")) }
    single<OptionsManager> { OptionsManagerImpl() }
    single<OptionDescriptorFactory> { OptionDescriptorFactoryImpl() }
    single<ToastFactory> { ToastFactoryImpl() }
}