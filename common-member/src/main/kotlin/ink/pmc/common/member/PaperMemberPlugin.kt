package ink.pmc.common.member

import ink.pmc.common.utils.isInDebugMode
import net.coreprotect.CoreProtect
import net.coreprotect.CoreProtectAPI
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.paper.PaperCommandManager
import java.io.File

lateinit var plugin: JavaPlugin
lateinit var paperCommandManager: PaperCommandManager<CommandSender>
lateinit var coreProtect: CoreProtect
lateinit var coreProtectAPI: CoreProtectAPI

@Suppress("UNUSED")
class PaperMemberPlugin : JavaPlugin() {

    override fun onEnable() {
        if (isInDebugMode()) {
            return
        }

        plugin = this
        disabled = false
        dataDir = dataFolder

        createDataDir()
        configFile = File(dataFolder, "config.toml")

        if (!configFile.exists()) {
            saveResource("config.toml", false)
        }

        initMemberManager()

        paperCommandManager = PaperCommandManager.createNative(
            this,
            ExecutionCoordinator.asyncCoordinator()
        )

        paperCommandManager.command(memberMigratorCommand)

        coreProtect = server.pluginManager.getPlugin("CoreProtect") as CoreProtect
        coreProtectAPI = coreProtect.api

        if (!coreProtectAPI.isEnabled) {
            logger.severe("CoreProtect API not enabled!")
        }
    }

    override fun onDisable() {
        if (isInDebugMode()) {
            return
        }

        disabled = true
    }

}