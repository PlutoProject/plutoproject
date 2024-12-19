package ink.pmc.framework.bridge.command.handlers.velocity

import com.velocitypowered.api.command.CommandSource
import ink.pmc.framework.bridge.command.handlers.bridgePlayerNotFound
import ink.pmc.framework.bridge.command.parsers.BridgePlayerNotFoundException
import ink.pmc.framework.chat.replace
import org.incendo.cloud.exception.handling.ExceptionContext
import org.incendo.cloud.exception.handling.ExceptionHandler

object BridgePlayerNotFoundHandler : ExceptionHandler<CommandSource, BridgePlayerNotFoundException> {
    override fun handle(context: ExceptionContext<CommandSource, BridgePlayerNotFoundException>) {
        context.context().sender().sendMessage(
            bridgePlayerNotFound.replace("<player>", context.exception().name)
        )
    }
}