package ink.pmc.framework.utils.command

import org.bukkit.command.CommandSender
import org.bukkit.plugin.Plugin
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.paper.LegacyPaperCommandManager

fun Plugin.commandManager(): LegacyPaperCommandManager<CommandSender> {
    return LegacyPaperCommandManager.createNative(
        this,
        ExecutionCoordinator.asyncCoordinator()
    ).apply { registerBrigadier() }
}