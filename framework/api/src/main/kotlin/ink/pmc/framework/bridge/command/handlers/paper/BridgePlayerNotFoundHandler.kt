package ink.pmc.framework.bridge.command.handlers.paper

import ink.pmc.framework.bridge.command.handlers.bridgePlayerNotFound
import ink.pmc.framework.bridge.command.parsers.BridgePlayerNotFoundException
import ink.pmc.framework.chat.replace
import org.bukkit.command.CommandSender
import org.incendo.cloud.exception.handling.ExceptionContext
import org.incendo.cloud.exception.handling.ExceptionHandler

object BridgePlayerNotFoundHandler : ExceptionHandler<CommandSender, BridgePlayerNotFoundException> {
    override fun handle(context: ExceptionContext<CommandSender, BridgePlayerNotFoundException>) {
        context.context().sender().sendMessage(
            bridgePlayerNotFound.replace("<player>", context.exception().name)
        )
    }
}