package ink.pmc.misc

import com.electronwill.nightconfig.core.file.FileConfig
import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import ink.pmc.misc.api.elevator.ElevatorManager
import ink.pmc.misc.api.head.HeadManager
import ink.pmc.misc.api.sit.SitManager
import ink.pmc.misc.commands.HeadCommand
import ink.pmc.misc.commands.SitCommand
import ink.pmc.misc.commands.SuicideCommand
import ink.pmc.misc.impl.elevator.ElevatorManagerImpl
import ink.pmc.misc.impl.elevator.builders.IronElevatorBuilder
import ink.pmc.misc.impl.head.HeadManagerImpl
import ink.pmc.misc.impl.sit.SitManagerImpl
import ink.pmc.misc.listeners.ChatListener
import ink.pmc.misc.listeners.CreeperAntiExplodeListener
import ink.pmc.misc.listeners.ElevatorListener
import ink.pmc.misc.listeners.SitListener
import ink.pmc.utils.BukkitCommandManager
import ink.pmc.utils.command.init
import ink.pmc.utils.command.mappers.Stack2SenderMapper
import ink.pmc.utils.inject.startKoinIfNotPresent
import org.bukkit.plugin.java.JavaPlugin
import org.incendo.cloud.execution.ExecutionCoordinator
import org.koin.dsl.module
import java.io.File

lateinit var commandManager: BukkitCommandManager
lateinit var plugin: JavaPlugin
lateinit var fileConfig: FileConfig
var disabled = true

@Suppress("UNUSED")
class PaperPlugin : JavaPlugin() {

    private val bukkitModule = module {
        single<SitManager> {
            check(MiscConfig.sit) { "Sit not enabled" }
            SitManagerImpl()
        }
        single<HeadManager> {
            check(MiscConfig.head) { "Head not enabled" }
            HeadManagerImpl()
        }
        single<ElevatorManager> {
            check(MiscConfig.elevator) { "Elevator not enabled" }
            ElevatorManagerImpl()
        }
    }

    override fun onEnable() {
        plugin = this
        disabled = false

        commandManager = BukkitCommandManager.builder(Stack2SenderMapper)
            .executionCoordinator(ExecutionCoordinator.asyncCoordinator())
            .buildOnEnable(this)

        val config = File(dataFolder, "config.conf")

        if (!config.exists()) {
            saveResource("config.conf", false)
        }

        fileConfig = config.loadConfig()
        loadConfigValues()

        startKoinIfNotPresent {
            modules(bukkitModule)
        }

        if (MiscConfig.sit) {
            server.pluginManager.registerSuspendingEvents(SitListener, this)
            runSitCheckTask()
            runActionBarOverrideTask()
        }

        if (MiscConfig.chat) {
            server.pluginManager.registerSuspendingEvents(ChatListener, this)
        }

        if (MiscConfig.elevator) {
            if (MiscConfig.elevatorRegisterDefaultBuilder) {
                ElevatorManager.registerBuilder(IronElevatorBuilder)
            }
            server.pluginManager.registerSuspendingEvents(ElevatorListener, this)
        }

        if (MiscConfig.creeperAntiExplode) {
            server.pluginManager.registerSuspendingEvents(CreeperAntiExplodeListener, this)
        }

        if (MiscConfig.head) {
            // 暂时没有
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
        SitManager.standAll()
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