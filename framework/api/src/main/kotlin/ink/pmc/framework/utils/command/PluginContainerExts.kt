package ink.pmc.framework.utils.command

import com.github.shynixn.mccoroutine.velocity.SuspendingPluginContainer
import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.plugin.PluginContainer
import ink.pmc.framework.utils.platform.proxy
import org.incendo.cloud.SenderMapper
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.velocity.VelocityCommandManager

fun PluginContainer.commandManager(): VelocityCommandManager<CommandSource> {
    return VelocityCommandManager<CommandSource>(
        this,
        proxy,
        ExecutionCoordinator.asyncCoordinator(),
        SenderMapper.identity()
    )
}

fun SuspendingPluginContainer.commandManager(): VelocityCommandManager<CommandSource> {
    return this.pluginContainer.commandManager()
}