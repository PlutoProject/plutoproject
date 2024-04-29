package ink.pmc.common.hypervisor

import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import ink.pmc.common.hypervisor.commands.HypervisorCommand
import ink.pmc.common.hypervisor.commands.StatusCommand
import ink.pmc.common.hypervisor.listeners.EntityListener
import ink.pmc.common.hypervisor.listeners.PlayerListener
import ink.pmc.common.utils.command.init
import me.lucko.spark.api.Spark
import me.lucko.spark.api.SparkProvider
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.paper.PaperCommandManager

lateinit var commandManager: PaperCommandManager<CommandSender>
lateinit var plugin: JavaPlugin
lateinit var spark: Spark
var disabled = true

@Suppress("UNUSED")
class PaperHypervisorPlugin : JavaPlugin() {

    override fun onEnable() {
        plugin = this
        disabled = false

        commandManager = PaperCommandManager.createNative(
            this,
            ExecutionCoordinator.asyncCoordinator()
        )

        spark = SparkProvider.get()

        commandManager.registerBrigadier()
        commandManager.init(HypervisorCommand)
        commandManager.init(StatusCommand)
        server.pluginManager.registerSuspendingEvents(PlayerListener, this)
        server.pluginManager.registerSuspendingEvents(EntityListener, this)
    }

    override fun onDisable() {
        disabled = true
    }

}