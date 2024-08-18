package ink.pmc.daily

import com.electronwill.nightconfig.core.file.FileConfig
import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.mongodb.kotlin.client.coroutine.MongoCollection
import ink.pmc.daily.api.Daily
import ink.pmc.daily.repositories.HistoryRepository
import ink.pmc.daily.repositories.PlayerRepository
import ink.pmc.provider.ProviderService
import ink.pmc.utils.PaperCm
import ink.pmc.utils.command.registerCommands
import ink.pmc.utils.inject.startKoinIfNotPresent
import ink.pmc.utils.storage.saveResourceIfNotExisted
import org.incendo.cloud.execution.ExecutionCoordinator
import org.koin.dsl.module
import java.io.File

internal const val COMMANDS_PACKAGE = "ink.pmc.daily.commands"
internal const val COLLECTION_PREFIX = "daily_"

private inline fun <reified T : Any> getCollection(name: String): MongoCollection<T> {
    return ProviderService.defaultMongoDatabase.getCollection("${COLLECTION_PREFIX}$name")
}

private val bukkitModule = module {
    single<Daily> { DailyImpl() }
    single<DailyConfig> { DailyConfig(fileConfig) }
    single<HistoryRepository> { HistoryRepository(getCollection("history")) }
    single<PlayerRepository> { PlayerRepository(getCollection("player")) }
}

internal lateinit var plugin: PaperPlugin
internal lateinit var fileConfig: FileConfig
internal lateinit var commandManager: PaperCm

@Suppress("UNUSED")
class PaperPlugin : SuspendingJavaPlugin() {

    override fun onEnable() {
        plugin = this
        fileConfig = saveResourceIfNotExisted("config.conf").loadConfig()

        startKoinIfNotPresent {
            modules(bukkitModule)
        }

        commandManager = PaperCm.builder()
            .executionCoordinator(ExecutionCoordinator.asyncCoordinator())
            .buildOnEnable(this)
            .also { it.registerCommands(COMMANDS_PACKAGE) }
    }

    private fun File.loadConfig(): FileConfig {
        return FileConfig.builder(this)
            .async()
            .autoreload()
            .build()
            .also { it.load() }
    }

    internal fun reload() {
        fileConfig.load()
    }

}