package ink.pmc.utils

import com.velocitypowered.api.command.CommandSource
import org.bukkit.command.CommandSender
import org.incendo.cloud.context.CommandContext
import org.incendo.cloud.paper.PaperCommandManager
import org.incendo.cloud.velocity.VelocityCommandManager

typealias BukkitCommandManager = PaperCommandManager<CommandSender>
typealias VelocityCommandManager = VelocityCommandManager<CommandSource>

typealias BukkitCommandContext = CommandContext<CommandSender>
typealias VelocityCommandContext = CommandContext<CommandSource>
