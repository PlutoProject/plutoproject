package ink.pmc.transfer.commands

import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.proxy.Player
import ink.pmc.transfer.AbstractProxyTransferService
import ink.pmc.transfer.DESTINATION_NOT_EXISTED
import ink.pmc.transfer.velocityCommandManager
import ink.pmc.utils.chat.NON_PLAYER
import ink.pmc.utils.chat.replace
import ink.pmc.utils.command.VelocityCommand
import net.kyori.adventure.text.Component
import org.incendo.cloud.component.CommandComponent
import org.incendo.cloud.kotlin.coroutines.extension.suspendingHandler
import org.incendo.cloud.kotlin.extension.commandBuilder
import org.incendo.cloud.parser.standard.StringParser
import org.incendo.cloud.suggestion.Suggestion
import java.util.concurrent.CompletableFuture

class TransferCommand(private val service: AbstractProxyTransferService) : VelocityCommand() {

    private fun destinations(argName: String = "dest") =
        CommandComponent.builder<CommandSource, String>()
            .suggestionProvider { _, _ ->
                CompletableFuture.completedFuture(
                    service.destinations.map {
                        Suggestion.suggestion(it.id)
                    }
                )
            }
            .parser(StringParser.stringParser())
            .name(argName)
            .required()

    private val transferSelf = velocityCommandManager.commandBuilder("transfer") {
        argument(destinations())
        suspendingHandler {
            val sender = it.sender()
            val dest = it.flags().get<String>("dest")!!
            val destination = service.getDestination(dest)

            if (sender !is Player) {
                sender.sendMessage(NON_PLAYER)
                return@suspendingHandler
            }

            if (destination == null) {
                sender.sendMessage(
                    DESTINATION_NOT_EXISTED
                        .replace("<name>", Component.text(dest))
                )
                return@suspendingHandler
            }

        }
    }.commandBuilder

    init {
        command(transferSelf)
    }

}