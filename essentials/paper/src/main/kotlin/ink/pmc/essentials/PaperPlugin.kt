package ink.pmc.essentials

import com.electronwill.nightconfig.core.file.FileConfig
import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import ink.pmc.utils.storage.saveResourceIfNotExisted
import io.github.classgraph.ClassGraph
import io.papermc.paper.command.brigadier.CommandSourceStack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import org.bukkit.plugin.Plugin
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.paper.PaperCommandManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import java.io.File
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.kotlinFunction

typealias Cm = PaperCommandManager<CommandSourceStack>

var disabled = true
lateinit var plugin: Plugin
lateinit var fileConfig: FileConfig
lateinit var commandManager: Cm

private const val COMMAND_PACKAGE = "ink.pmc.essentials.commands"
val essentialsScope = CoroutineScope(Dispatchers.Default)

@Suppress("UNUSED")
class PaperPlugin : SuspendingJavaPlugin(), KoinComponent {

    private val conf by inject<EssentialsConfig>()

    override suspend fun onEnableAsync() {
        plugin = this

        startKoin {
            modules(appModule)
        }

        val config = saveResourceIfNotExisted("config.conf")
        fileConfig = config.loadConfig()

        commandManager = PaperCommandManager.builder()
            .executionCoordinator(ExecutionCoordinator.asyncCoordinator())
            .buildOnEnable(this)

        commandManager.registerCommands(COMMAND_PACKAGE)
        disabled = false
    }

    override suspend fun onDisableAsync() {
        disabled = true
        essentialsScope.cancel()
    }

    private fun File.loadConfig(): FileConfig {
        return FileConfig.builder(this)
            .async()
            .autoreload()
            .build()
            .apply { load() }
    }

    private fun PaperCommandManager<CommandSourceStack>.registerCommands(packageName: String) {
        val scanResult = ClassGraph()
            .acceptPackages(packageName)
            .scan()

        scanResult.allClasses.forEach {
            val cls = Class.forName(it.name)
            cls.declaredMethods.forEach fns@{ fn ->
                val function = fn.kotlinFunction ?: return@fns
                val annotation = function.findAnnotation<Command>() ?: return@fns
                val name = annotation.name

                if (!conf.Commands()[name]) {
                    return@fns
                }

                val aliases = conf.CommandAliases()[name]
                function.call(this, aliases)
            }
        }
    }

}