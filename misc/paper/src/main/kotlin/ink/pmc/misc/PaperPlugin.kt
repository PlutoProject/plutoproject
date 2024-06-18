package ink.pmc.misc

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
import ink.pmc.utils.command.init
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.paper.PaperCommandManager

lateinit var commandManager: PaperCommandManager<CommandSourceStack>
lateinit var plugin: JavaPlugin
lateinit var sitManager: SitManager
lateinit var elevatorManager: ElevatorManager
lateinit var headManager: HeadManager
var disabled = true

@Suppress("UNUSED")
class PaperPlugin : JavaPlugin() {

    override fun onEnable() {
        plugin = this
        disabled = false

        commandManager = PaperCommandManager.builder()
            .executionCoordinator(ExecutionCoordinator.asyncCoordinator())
            .buildOnEnable(this)

        sitManager = SitManagerImpl()
        elevatorManager = ElevatorManagerImpl()
        headManager = HeadManagerImpl()

        MiscAPI.instance = MiscAPIImpl
        MiscAPIImpl.internalSitManager = sitManager
        MiscAPIImpl.internalElevatorManager = elevatorManager
        MiscAPIImpl.internalHeadManager = headManager

        elevatorManager.registerBuilder(IronElevatorBuilder)

        // commandManager.registerBrigadier()
        commandManager.init(SuicideCommand)
        commandManager.init(SitCommand)
        commandManager.init(HeadCommand)

        runSitCheckTask()
        runActionBarOverrideTask()

        server.pluginManager.registerSuspendingEvents(Listeners, this)
    }

    override fun onDisable() {
        sitManager.standAll()
        disabled = true
    }

}