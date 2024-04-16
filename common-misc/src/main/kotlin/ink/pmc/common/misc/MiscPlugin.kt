package ink.pmc.common.misc

import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import ink.pmc.common.misc.api.MiscAPI
import ink.pmc.common.misc.api.elevator.ElevatorManager
import ink.pmc.common.misc.api.head.HeadManager
import ink.pmc.common.misc.api.sit.SitManager
import ink.pmc.common.misc.commands.HeadCommand
import ink.pmc.common.misc.commands.SitCommand
import ink.pmc.common.misc.commands.SuicideCommand
import ink.pmc.common.misc.impl.MiscAPIImpl
import ink.pmc.common.misc.impl.elevator.ElevatorManagerImpl
import ink.pmc.common.misc.impl.elevator.builders.IronElevatorBuilder
import ink.pmc.common.misc.impl.head.HeadManagerImpl
import ink.pmc.common.misc.impl.sit.SitManagerImpl
import ink.pmc.common.utils.command.init
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.paper.PaperCommandManager

lateinit var commandManager: PaperCommandManager<CommandSender>
lateinit var plugin: JavaPlugin
lateinit var sitManager: SitManager
lateinit var elevatorManager: ElevatorManager
lateinit var headManager: HeadManager
var disabled = true

@Suppress("UNUSED")
class MiscPlugin : JavaPlugin() {

    override fun onEnable() {
        plugin = this
        disabled = false

        commandManager = PaperCommandManager.createNative(
            this,
            ExecutionCoordinator.asyncCoordinator()
        )

        sitManager = SitManagerImpl()
        elevatorManager = ElevatorManagerImpl()
        headManager = HeadManagerImpl()

        MiscAPI.instance = MiscAPIImpl
        MiscAPIImpl.internalSitManager = sitManager
        MiscAPIImpl.internalElevatorManager = elevatorManager
        MiscAPIImpl.internalHeadManager = headManager

        elevatorManager.registerBuilder(IronElevatorBuilder)

        commandManager.registerBrigadier()
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