package ink.pmc.hypervisor.commands

import ink.pmc.hypervisor.commandManager
import ink.pmc.utils.command.PaperCommand
import org.incendo.cloud.kotlin.coroutines.extension.suspendingHandler

object StatusCommand : PaperCommand() {

    private val status = commandManager.commandBuilder("status", "hvstatus")
        .permission("hypervisor.status")
        .suspendingHandler {
            status(it.sender())
        }

    init {
        // command(status)
    }

}