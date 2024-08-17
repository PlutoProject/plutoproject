package ink.pmc.daily

import com.electronwill.nightconfig.core.file.FileConfig
import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import ink.pmc.daily.api.Daily
import ink.pmc.daily.repositories.HistoryRepository
import ink.pmc.daily.repositories.PlayerRepository
import ink.pmc.utils.PaperCm
import ink.pmc.utils.command.registerCommands
import ink.pmc.utils.inject.startKoinIfNotPresent
import ink.pmc.utils.storage.saveResourceIfNotExisted
import org.incendo.cloud.execution.ExecutionCoordinator
import org.koin.dsl.module
import java.io.File

private val bukkitModule = module {
    single<HistoryRepository> { HistoryRepository() }
    single<PlayerRepository> { PlayerRepository() }
    single<Daily> { DailyImpl() }
    single<DailyConfig> { DailyConfig(fileConfig) }
}

internal const val COMMANDS_PACKAGE = "ink.pmc.daily.commands"

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