package ink.pmc.menu

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import ink.pmc.utils.command.registerCommands
import io.papermc.paper.command.brigadier.CommandSourceStack
import net.milkbowl.vault.economy.Economy
import org.bukkit.plugin.java.JavaPlugin
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.paper.PaperCommandManager

private const val COMMANDS = "ink.pmc.menu.commands"

lateinit var plugin: JavaPlugin
lateinit var commandManager: PaperCommandManager<CommandSourceStack>
lateinit var economy: Economy

@Suppress("UNUSED")
class PaperPlugin : SuspendingJavaPlugin() {

    override suspend fun onEnableAsync() {
        plugin = this

        commandManager = PaperCommandManager.builder()
            .executionCoordinator(ExecutionCoordinator.asyncCoordinator())
            .buildOnEnable(this)
        commandManager.registerCommands(COMMANDS)

        server.servicesManager.getRegistration(Economy::class.java)?.provider?.also { economy = it }
    }

}