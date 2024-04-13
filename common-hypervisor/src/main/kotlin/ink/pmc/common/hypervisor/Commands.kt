package ink.pmc.common.hypervisor

import ink.pmc.common.utils.chat.EMPTY_LINE
import ink.pmc.common.utils.concurrent.submitSync
import org.bukkit.command.CommandSender

fun status(sender: CommandSender) {
    submitSync {
        sender.sendMessage(SERVER_STATUS)
        sender.sendMessage(EMPTY_LINE)
        sender.sendMessage(WORLD_STATUS)
        sender.sendMessage(WORLD_STATUS_ENTRIES)
    }
}

fun serverStatus(sender: CommandSender) {
    submitSync {
        sender.sendMessage(SERVER_STATUS)
    }
}

fun worldStatus(sender: CommandSender) {
    submitSync {
        sender.sendMessage(WORLD_STATUS)
        sender.sendMessage(WORLD_STATUS_ENTRIES)
    }
}

val statusCommand = commandManager.commandBuilder("hypervisor", "hv")
    .permission("hypervisor.status")
    .literal("status")
    .handler {
        status(it.sender())
    }!!

val serverStatusCommand = commandManager.commandBuilder("hypervisor", "hv")
    .permission("hypervisor.status")
    .literal("serverstatus")
    .handler {
        serverStatus(it.sender())
    }!!

val worldStatusCommand = commandManager.commandBuilder("hypervisor", "hv")
    .permission("hypervisor.status")
    .literal("worldstatus")
    .handler {
        worldStatus(it.sender())
    }!!

val standaloneStatusCommand = commandManager.commandBuilder("status", "hvstatus")
    .permission("hypervisor.status")
    .handler {
        status(it.sender())
    }!!