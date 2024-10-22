package ink.pmc.hypervisor

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import com.sksamuel.hoplite.PropertySource
import ink.pmc.hypervisor.StatisticProviderType.NATIVE
import ink.pmc.hypervisor.StatisticProviderType.SPARK
import ink.pmc.hypervisor.commands.*
import ink.pmc.hypervisor.config.HypervisorConfig
import ink.pmc.hypervisor.listeners.DynamicSchedulingListener
import ink.pmc.hypervisor.providers.NativeStatisticProvider
import ink.pmc.hypervisor.providers.SparkStatisticProvider
import ink.pmc.options.api.OptionsManager
import ink.pmc.utils.command.init
import ink.pmc.utils.config.preconfiguredConfigLoaderBuilder
import ink.pmc.utils.inject.startKoinIfNotPresent
import ink.pmc.utils.jvm.findClass
import ink.pmc.utils.storage.saveResourceIfNotExisted
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.plugin.java.JavaPlugin
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.paper.PaperCommandManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.dsl.module
import java.util.logging.Logger

lateinit var commandManager: PaperCommandManager<CommandSourceStack>
lateinit var plugin: JavaPlugin
lateinit var pluginLogger: Logger
var disabled = true

@Suppress("UNUSED")
class PaperPlugin : SuspendingJavaPlugin(), KoinComponent {
    private val config by inject<HypervisorConfig>()
    private val bukkitModule = module {
        single<HypervisorConfig> {
            preconfiguredConfigLoaderBuilder()
                .addPropertySource(PropertySource.file(saveResourceIfNotExisted("config.conf")))
                .build()
                .loadConfigOrThrow()
        }
        single<StatisticProvider> {
            when (config.statisticProvider) {
                NATIVE -> NativeStatisticProvider()
                SPARK -> if (findClass("me.lucko.spark.api.Spark") != null) {
                    SparkStatisticProvider(SparkHook.instance)
                } else {
                    logger.warn("Spark API not found, fallback to native statistic provider")
                    NativeStatisticProvider()
                }
            }
        }
        single<Hypervisor> { HypervisorImpl() }
        single<DynamicScheduling> { DynamicSchedulingImpl() }
    }

    override fun onEnable() {
        plugin = this
        pluginLogger = logger

        startKoinIfNotPresent {
            modules(bukkitModule)
        }

        commandManager = PaperCommandManager.builder()
            .executionCoordinator(ExecutionCoordinator.asyncCoordinator())
            .buildOnEnable(this)
        commandManager.init(HypervisorCommand)
        commandManager.init(StatusCommand)
        commandManager.command(debugCommand)
        commandManager.command(enabledCommand)
        commandManager.command(disabledCommand)

        OptionsManager.registerOptionDescriptor(DYNAMIC_VIEW_DISTANCE)

        if (config.dynamicScheduling.enabled) {
            server.pluginManager.registerSuspendingEvents(DynamicSchedulingListener, this)
            DynamicScheduling.start()
        }

        disabled = false
    }

    override fun onDisable() {
        if (config.dynamicScheduling.enabled) {
            DynamicScheduling.stop()
        }
        disabled = true
    }

}