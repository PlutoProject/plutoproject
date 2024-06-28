package ink.pmc.hypervisor

import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import ink.pmc.hypervisor.commands.HypervisorCommand
import ink.pmc.hypervisor.commands.StatusCommand
import ink.pmc.hypervisor.listeners.EntityListener
import ink.pmc.hypervisor.listeners.PlayerListener
import ink.pmc.utils.command.init
import io.papermc.paper.command.brigadier.CommandSourceStack
import me.lucko.spark.api.Spark
import me.lucko.spark.api.SparkProvider
import org.bukkit.plugin.java.JavaPlugin
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.paper.PaperCommandManager
import java.util.logging.Logger

lateinit var commandManager: PaperCommandManager<CommandSourceStack>
lateinit var plugin: JavaPlugin
lateinit var spark: Spark
lateinit var serverLogger: Logger
var disabled = true

@Suppress("UNUSED")
class PaperPlugin : JavaPlugin() {

    override fun onEnable() {
        plugin = this
        disabled = false
        serverLogger = logger

        commandManager = PaperCommandManager.builder()
            .executionCoordinator(ExecutionCoordinator.asyncCoordinator())
            .buildOnEnable(this)

        spark = SparkProvider.get()

        // commandManager.registerBrigadier()
        commandManager.init(HypervisorCommand)
        commandManager.init(StatusCommand)
        server.pluginManager.registerSuspendingEvents(PlayerListener, this)
        server.pluginManager.registerSuspendingEvents(EntityListener, this)
        // Risky
        // server.pluginManager.registerSuspendingEvents(DuplicatedUuidDetector, this)
    }

    override fun onDisable() {
        disabled = true
    }
}