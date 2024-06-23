package ink.pmc.hypervisor.commands

import ink.pmc.hypervisor.commandManager
import ink.pmc.utils.command.PaperCommand
import org.incendo.cloud.kotlin.coroutines.extension.suspendingHandler

object HypervisorCommand : PaperCommand() {

    private val hypervisorStatus = commandManager.commandBuilder("hypervisor", "hv")
        .permission("hypervisor.status")
        .literal("status")
        .suspendingHandler {
            status(it.sender().sender)
        }

    private val hypervisorServerStatus = commandManager.commandBuilder("hypervisor", "hv")
        .permission("hypervisor.status")
        .literal("serverstatus")
        .suspendingHandler {
            serverStatus(it.sender().sender)
        }

    private val hypervisorWorldStatus = commandManager.commandBuilder("hypervisor", "hv")
        .permission("hypervisor.status")
        .literal("worldstatus")
        .suspendingHandler {
            worldStatus(it.sender().sender)
        }

    init {
        command(hypervisorStatus)
        command(hypervisorServerStatus)
        command(hypervisorWorldStatus)
    }

}