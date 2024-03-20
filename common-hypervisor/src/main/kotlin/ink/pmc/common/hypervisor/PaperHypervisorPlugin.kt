package ink.pmc.common.hypervisor

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
        commandManager.command(statusCommand)
        commandManager.command(serverStatusCommand)
        commandManager.command(worldStatusCommand)
        commandManager.command(standaloneStatusCommand)
    }

    override fun onDisable() {
        disabled = true
    }

}