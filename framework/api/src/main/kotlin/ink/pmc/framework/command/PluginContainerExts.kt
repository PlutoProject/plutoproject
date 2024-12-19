package ink.pmc.framework.command

import com.github.shynixn.mccoroutine.velocity.SuspendingPluginContainer
import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.plugin.PluginContainer
import ink.pmc.framework.bridge.command.handlers.velocity.BridgePlayerNotFoundHandler
import ink.pmc.framework.bridge.command.handlers.velocity.BridgeServerNotFoundHandler
import ink.pmc.framework.bridge.command.parsers.BridgePlayerNotFoundException
import ink.pmc.framework.bridge.command.parsers.BridgeServerNotFoundException
import ink.pmc.framework.bridge.command.parsers.bridgePlayerParser
import ink.pmc.framework.bridge.command.parsers.bridgeServerParser
import ink.pmc.framework.platform.proxy
import org.incendo.cloud.SenderMapper
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.velocity.VelocityCommandManager

fun PluginContainer.commandManager(): VelocityCommandManager<CommandSource> {
    return VelocityCommandManager(
        this,
        proxy,
        ExecutionCoordinator.asyncCoordinator(),
        SenderMapper.identity()
    ).apply {
        parserRegistry().apply {
            registerParser(bridgePlayerParser())
            registerParser(bridgeServerParser())
        }
        exceptionController().apply {
            registerHandler(BridgePlayerNotFoundException::class.java, BridgePlayerNotFoundHandler)
            registerHandler(BridgeServerNotFoundException::class.java, BridgeServerNotFoundHandler)
        }
    }
}

fun SuspendingPluginContainer.commandManager(): VelocityCommandManager<CommandSource> {
    return this.pluginContainer.commandManager()
}