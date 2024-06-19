package ink.pmc.transfer.commands

import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.proxy.Player
import ink.pmc.transfer.*
import ink.pmc.transfer.api.DestinationStatus
import ink.pmc.utils.chat.NON_PLAYER
import ink.pmc.utils.chat.replace
import ink.pmc.utils.command.VelocityCommand
import ink.pmc.utils.multiplaform.player.velocity.wrapped
import ink.pmc.utils.visual.mochaFlamingo
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
                        .replace("<id>", Component.text(dest))
                )
                return@suspendingHandler
            }

            if (destination.status == DestinationStatus.MAINTENANCE) {
                sender.sendMessage(TRANSFER_FAILED_SERVER_MAINTENACE)
                return@suspendingHandler
            }

            if (destination.status == DestinationStatus.OFFLINE) {
                sender.sendMessage(TRANSFER_FAILED_SERVER_OFFLINE)
                return@suspendingHandler
            }

            val condition = service.conditionManager.verifyCondition(sender.wrapped, destination)

            if (!condition) {
                sender.sendMessage(TRANSFER_FAILED_CONDITIONAL)
                return@suspendingHandler
            }

            service.transferPlayer(sender.wrapped, destination.id)
            sender.sendMessage(
                TRANSFER_SUCCEED
                    .replace("<name>", destination.name.color(mochaFlamingo))
            )
        }
    }.commandBuilder

    init {
        command(transferSelf)
    }

}