package ink.pmc.hypervisor

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import ink.pmc.hypervisor.commands.HypervisorCommand
import ink.pmc.hypervisor.commands.StatusCommand
import ink.pmc.hypervisor.providers.NativeStatisticProvider
import ink.pmc.hypervisor.providers.SparkStatisticProvider
import ink.pmc.utils.command.init
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.paper.PaperCommandManager
import org.koin.dsl.module
import java.util.logging.Logger

lateinit var commandManager: PaperCommandManager<CommandSourceStack>
lateinit var plugin: JavaPlugin
lateinit var serverLogger: Logger
var disabled = true

@Suppress("UNUSED")
class PaperPlugin : SuspendingJavaPlugin() {
    private val bukkitModule = module {
        single<StatisticProvider> {
            if (Bukkit.getPluginManager().getPlugin("spark") != null) {
                SparkStatisticProvider(SparkHook.instance)
            } else {
                NativeStatisticProvider()
            }
        }
        single<Hypervisor> { HypervisorImpl() }
    }

    override fun onEnable() {
        plugin = this
        disabled = false
        serverLogger = logger

        commandManager = PaperCommandManager.builder()
            .executionCoordinator(ExecutionCoordinator.asyncCoordinator())
            .buildOnEnable(this)

        commandManager.init(HypervisorCommand)
        commandManager.init(StatusCommand)
    }

    override fun onDisable() {
        disabled = true
    }

}