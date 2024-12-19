package ink.pmc.framework.command

import ink.pmc.framework.bridge.command.handlers.paper.BridgePlayerNotFoundHandler
import ink.pmc.framework.bridge.command.handlers.paper.BridgeServerNotFoundHandler
import ink.pmc.framework.bridge.command.parsers.BridgePlayerNotFoundException
import ink.pmc.framework.bridge.command.parsers.BridgeServerNotFoundException
import ink.pmc.framework.bridge.command.parsers.bridgePlayerParser
import ink.pmc.framework.bridge.command.parsers.bridgeServerParser
import org.bukkit.command.CommandSender
import org.bukkit.plugin.Plugin
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.paper.LegacyPaperCommandManager

fun Plugin.commandManager(): LegacyPaperCommandManager<CommandSender> {
    return LegacyPaperCommandManager.createNative(
        this,
        ExecutionCoordinator.asyncCoordinator()
    ).apply {
        registerBrigadier()
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