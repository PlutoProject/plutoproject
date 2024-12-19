package ink.pmc.framework.bridge.command.handlers.paper

import ink.pmc.framework.bridge.command.handlers.bridgeServerNotFound
import ink.pmc.framework.bridge.command.parsers.BridgeServerNotFoundException
import ink.pmc.framework.chat.replace
import org.bukkit.command.CommandSender
import org.incendo.cloud.exception.handling.ExceptionContext
import org.incendo.cloud.exception.handling.ExceptionHandler

object BridgeServerNotFoundHandler : ExceptionHandler<CommandSender, BridgeServerNotFoundException> {
    override fun handle(context: ExceptionContext<CommandSender, BridgeServerNotFoundException>) {
        context.context().sender().sendMessage(
            bridgeServerNotFound.replace("<server>", context.exception().id)
        )
    }
}