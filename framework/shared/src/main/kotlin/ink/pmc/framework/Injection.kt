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
import ink.pmc.framework.utils.config.preconfiguredConfigLoaderBuilder
import ink.pmc.framework.visual.ToastFactoryImpl
import ink.pmc.framework.visual.toast.ToastFactory
import ink.pmc.framework.options.OptionsManager
import ink.pmc.framework.options.factory.OptionDescriptorFactory
import ink.pmc.framework.playerdb.PlayerDb
import ink.pmc.framework.provider.Provider
import ink.pmc.framework.rpc.RpcClient
import ink.pmc.framework.rpc.RpcServer
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