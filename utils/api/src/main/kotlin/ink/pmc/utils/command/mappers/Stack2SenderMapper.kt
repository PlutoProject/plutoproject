package ink.pmc.utils.command.mappers

import ink.pmc.utils.entity.internal
import ink.pmc.utils.platform.minecraftServer
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.command.CommandSender
import org.bukkit.entity.Entity
import org.incendo.cloud.SenderMapper

object Stack2SenderMapper : SenderMapper<CommandSourceStack, CommandSender> {
    override fun map(base: CommandSourceStack): CommandSender {
        return base.sender
    }

    override fun reverse(mapped: CommandSender): CommandSourceStack {
        return when (mapped) {
            is Entity -> mapped.internal.createCommandSourceStack()
            else -> minecraftServer.createCommandSourceStack()
        }
    }
}