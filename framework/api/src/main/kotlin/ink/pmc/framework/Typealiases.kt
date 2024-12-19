package ink.pmc.framework

import com.velocitypowered.api.command.CommandSource
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.incendo.cloud.context.CommandContext
import org.incendo.cloud.paper.PaperCommandManager
import org.incendo.cloud.velocity.VelocityCommandManager

typealias PaperCm = PaperCommandManager<CommandSourceStack>
typealias PaperCtx = CommandContext<CommandSourceStack>
typealias VelocityCm = VelocityCommandManager<CommandSource>
typealias VelocityCtx = CommandContext<CommandSource>