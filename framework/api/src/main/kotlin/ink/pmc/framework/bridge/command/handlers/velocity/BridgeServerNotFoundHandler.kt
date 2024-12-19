package ink.pmc.framework.bridge.command.handlers.velocity

import com.velocitypowered.api.command.CommandSource
import ink.pmc.framework.bridge.command.handlers.bridgeServerNotFound
import ink.pmc.framework.bridge.command.parsers.BridgeServerNotFoundException
import ink.pmc.framework.chat.replace
import org.incendo.cloud.exception.handling.ExceptionContext
import org.incendo.cloud.exception.handling.ExceptionHandler

object BridgeServerNotFoundHandler : ExceptionHandler<CommandSource, BridgeServerNotFoundException> {
    override fun handle(context: ExceptionContext<CommandSource, BridgeServerNotFoundException>) {
        context.context().sender().sendMessage(
            bridgeServerNotFound.replace("<server>", context.exception().id)
        )
    }
}