package ink.pmc.hypervisor

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import ink.pmc.hypervisor.commands.HypervisorCommand
import ink.pmc.hypervisor.commands.StatusCommand
import ink.pmc.utils.BukkitCommandManager
import ink.pmc.utils.command.init
import ink.pmc.utils.command.mappers.Stack2SenderMapper
import me.lucko.spark.api.Spark
import me.lucko.spark.api.SparkProvider
import org.bukkit.plugin.java.JavaPlugin
import org.incendo.cloud.execution.ExecutionCoordinator
import java.util.logging.Logger

lateinit var commandManager: BukkitCommandManager
lateinit var plugin: JavaPlugin
lateinit var spark: Spark
lateinit var serverLogger: Logger
var disabled = true

@Suppress("UNUSED")
class PaperPlugin : SuspendingJavaPlugin() {

    override fun onEnable() {
        plugin = this
        disabled = false
        serverLogger = logger

        commandManager = BukkitCommandManager.builder(Stack2SenderMapper)
            .executionCoordinator(ExecutionCoordinator.asyncCoordinator())
            .buildOnEnable(this)

        spark = SparkProvider.get()
        commandManager.init(HypervisorCommand)
        commandManager.init(StatusCommand)
    }

    override fun onDisable() {
        disabled = true
    }

}