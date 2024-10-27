package ink.pmc.misc

import com.electronwill.nightconfig.core.file.FileConfig
import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import ink.pmc.framework.utils.command.annotationParser
import ink.pmc.framework.utils.command.commandManager
import ink.pmc.framework.utils.inject.startKoinIfNotPresent
import ink.pmc.misc.api.elevator.ElevatorManager
import ink.pmc.misc.api.sit.SitManager
import ink.pmc.misc.commands.SitCommand
import ink.pmc.misc.commands.SuicideCommand
import ink.pmc.misc.impl.elevator.ElevatorManagerImpl
import ink.pmc.misc.impl.elevator.builders.IronElevatorBuilder
import ink.pmc.misc.impl.sit.SitManagerImpl
import ink.pmc.misc.listeners.ChatListener
import ink.pmc.misc.listeners.CreeperAntiExplodeListener
import ink.pmc.misc.listeners.ElevatorListener
import ink.pmc.misc.listeners.SitListener
import org.bukkit.plugin.java.JavaPlugin
import org.koin.dsl.module
import java.io.File

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
        single<ElevatorManager> {
            check(MiscConfig.elevator) { "Elevator not enabled" }
            ElevatorManagerImpl()
        }
    }

    override fun onEnable() {
        plugin = this
        disabled = false

        commandManager().annotationParser().apply {
            if (MiscConfig.commandSuicide) parse(SuicideCommand)
            if (MiscConfig.commandSit) parse(SitCommand)
        }

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