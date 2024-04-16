package ink.pmc.common.hypervisor.commands

import ink.pmc.common.hypervisor.SERVER_STATUS
import ink.pmc.common.hypervisor.WORLD_STATUS
import ink.pmc.common.hypervisor.WORLD_STATUS_ENTRIES
import ink.pmc.common.utils.chat.EMPTY_LINE
import ink.pmc.common.utils.concurrent.sync
import org.bukkit.command.CommandSender

suspend fun status(sender: CommandSender) {
    sync {
        sender.sendMessage(SERVER_STATUS)
        sender.sendMessage(EMPTY_LINE)
        sender.sendMessage(WORLD_STATUS)
        sender.sendMessage(WORLD_STATUS_ENTRIES)
    }
}

suspend fun serverStatus(sender: CommandSender) {
    sync {
        sender.sendMessage(SERVER_STATUS)
    }
}

suspend fun worldStatus(sender: CommandSender) {
    sync {
        sender.sendMessage(WORLD_STATUS)
        sender.sendMessage(WORLD_STATUS_ENTRIES)
    }
}