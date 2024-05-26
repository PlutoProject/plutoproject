package ink.pmc.rpc.commands

import ink.pmc.utils.chat.replace
import ink.pmc.utils.command.VelocityCommand
import ink.pmc.utils.visual.mochaText
import ink.pmc.rpc.*
import net.kyori.adventure.text.Component
import org.incendo.cloud.kotlin.coroutines.extension.suspendingHandler

object RpcServerCommand : VelocityCommand() {

    private val rpcServerServices = velocityCommandManager.commandBuilder("rpcserver", "rpcs")
        .permission("rpc.commands")
        .literal("services")
        .suspendingHandler {
            val sender = it.sender()

            if (rpcServer.server.services.isEmpty()) {
                sender.sendMessage(RPC_SERVER_SERVICES_EMPTY)
                return@suspendingHandler
            }

            sender.sendMessage(RPC_SERVER_SERVICES)

            rpcServer.server.services.forEach { service ->
                val name = service.serviceDescriptor.name
                sender.sendMessage(
                    RPC_SERVER_SERVICES_ENTRY
                        .replace("<name>", Component.text(name).color(mochaText))
                )
            }
        }

    init {
        command(rpcServerServices)
    }

}