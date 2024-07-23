package ink.pmc.misc

import com.electronwill.nightconfig.core.file.FileConfig
import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import ink.pmc.misc.api.MiscAPI
import ink.pmc.misc.api.elevator.ElevatorManager
import ink.pmc.misc.api.head.HeadManager
import ink.pmc.misc.api.sit.SitManager
import ink.pmc.misc.commands.HeadCommand
import ink.pmc.misc.commands.SitCommand
import ink.pmc.misc.commands.SuicideCommand
import ink.pmc.misc.impl.MiscAPIImpl
import ink.pmc.misc.impl.elevator.ElevatorManagerImpl
import ink.pmc.misc.impl.elevator.builders.IronElevatorBuilder
import ink.pmc.misc.impl.head.HeadManagerImpl
import ink.pmc.misc.impl.sit.SitManagerImpl
import ink.pmc.misc.listeners.ChatListener
import ink.pmc.misc.listeners.CreeperAntiExplodeListener
import ink.pmc.misc.listeners.ElevatorListener
import ink.pmc.misc.listeners.SitListener
import ink.pmc.utils.command.init
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.plugin.java.JavaPlugin
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.paper.PaperCommandManager
import java.io.File

lateinit var commandManager: PaperCommandManager<CommandSourceStack>
lateinit var plugin: JavaPlugin
lateinit var sitManager: SitManager
lateinit var elevatorManager: ElevatorManager
lateinit var headManager: HeadManager
lateinit var fileConfig: FileConfig
var disabled = true

@Suppress("UNUSED")
class PaperPlugin : JavaPlugin() {

    override fun onEnable() {
        plugin = this
        disabled = false

        commandManager = PaperCommandManager.builder()
            .executionCoordinator(ExecutionCoordinator.asyncCoordinator())
            .buildOnEnable(this)

        val config = File(dataFolder, "config.conf")

        if (!config.exists()) {
            saveResource("config.conf", false)
        }

        fileConfig = config.loadConfig()
        loadConfigValues()

        MiscAPI.instance = MiscAPIImpl

        if (MiscConfig.sit) {
            sitManager = SitManagerImpl()
            MiscAPIImpl.internalSitManager = sitManager
            server.pluginManager.registerSuspendingEvents(SitListener, this)
            runSitCheckTask()
            runActionBarOverrideTask()
        }

        if (MiscConfig.chat) {
            server.pluginManager.registerSuspendingEvents(ChatListener, this)
        }

        if (MiscConfig.elevator) {
            elevatorManager = ElevatorManagerImpl()
            MiscAPIImpl.internalElevatorManager = elevatorManager
            if (MiscConfig.elevatorRegisterDefaultBuilder) {
                elevatorManager.registerBuilder(IronElevatorBuilder)
            }
            server.pluginManager.registerSuspendingEvents(ElevatorListener, this)
        }

        if (MiscConfig.creeperAntiExplode) {
            server.pluginManager.registerSuspendingEvents(CreeperAntiExplodeListener, this)
        }

        if (MiscConfig.head) {
            headManager = HeadManagerImpl()
            MiscAPIImpl.internalHeadManager = headManager
        }

        if (MiscConfig.commandSuicide) {
            commandManager.init(SuicideCommand)
        }

        if (MiscConfig.commandSit) {
            commandManager.init(SitCommand)
        }

        if (MiscConfig.commandHead) {
            commandManager.init(HeadCommand)
        }
    }

    override fun onDisable() {
        sitManager.standAll()
        disabled = true
    }

    private fun loadConfigValues() {
        MiscConfig.sit = fileConfig.get("sit.enabled")
        MiscConfig.head = fileConfig.get("head.enabled")
        MiscConfig.elevator = fileConfig.get("elevator.enabled")
        MiscConfig.elevatorRegisterDefaultBuilder = fileConfig.get("elevator.register-default-builder")
        MiscConfig.creeperAntiExplode = fileConfig.get("creeper-anti-explode.enabled")
        MiscConfig.creeperAntiExplodeFirework = fileConfig.get("creeper-anti-explode.firework")
        MiscConfig.chat = fileConfig.get("chat.enabled")
        MiscConfig.commandSuicide = fileConfig.get("commands.suicide")
        MiscConfig.commandSit = fileConfig.get("commands.sit")
        MiscConfig.commandHead = fileConfig.get("commands.head")
    }

    private fun File.loadConfig(): FileConfig {
        return FileConfig.builder(this)
            .async()
            .autoreload()
            .onAutoReload { loadConfigValues() }
            .build()
            .apply { load() }
    }

}