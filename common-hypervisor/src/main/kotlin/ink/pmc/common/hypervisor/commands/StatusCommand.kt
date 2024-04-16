package ink.pmc.common.hypervisor.commands

import ink.pmc.common.hypervisor.commandManager
import ink.pmc.common.utils.command.PaperCommand
import org.incendo.cloud.kotlin.coroutines.extension.suspendingHandler

object StatusCommand : PaperCommand() {

    private val status = commandManager.commandBuilder("status", "hvstatus")
        .permission("hypervisor.status")
        .suspendingHandler {
            status(it.sender())
        }

    init {
        command(status)
    }

}