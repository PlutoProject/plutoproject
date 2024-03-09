package ink.pmc.common.misc

import ink.pmc.common.misc.listeners.PlayerListener
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.paper.PaperCommandManager

lateinit var commandManager: PaperCommandManager<CommandSender>
lateinit var plugin: JavaPlugin

class MiscPlugin : JavaPlugin() {

    override fun onEnable() {
        plugin = this

        commandManager = PaperCommandManager.createNative(
            this,
            ExecutionCoordinator.asyncCoordinator()
        )

        commandManager.registerBrigadier()
        commandManager.command(suicideCommand)

        server.pluginManager.registerEvents(PlayerListener, this)
    }

}