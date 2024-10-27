package ink.pmc.essentials.commands.warp

import ink.pmc.advkt.component.replace
import ink.pmc.essentials.COMMAND_WARP_NOT_EXISTED
import ink.pmc.essentials.api.warp.Warp
import ink.pmc.essentials.api.warp.WarpManager
import ink.pmc.essentials.commands.parseWarp
import org.bukkit.command.CommandSender
import org.incendo.cloud.annotations.suggestion.Suggestions
import org.incendo.cloud.context.CommandContext
import org.incendo.cloud.context.CommandInput

suspend fun CommandSender.parseAndCheck(name: String): Warp? {
    return parseWarp(name) ?: run {
        sendMessage(COMMAND_WARP_NOT_EXISTED.replace("<name>", name))
        null
    }
}

@Suppress("UNUSED")
object WarpCommons {
    @Suppress("UnusedReceiverParameter", "UNUSED_PARAMETER")
    @Suggestions("warps")
    internal suspend fun CommandContext<CommandSender>.warps(input: CommandInput): List<String> {
        return WarpManager.list().map {
            val name = it.name
            val alias = it.alias
            if (alias == null) name else "$name-$alias"
        }
    }

    @Suppress("UnusedReceiverParameter", "UNUSED_PARAMETER")
    @Suggestions("warps-without-alias")
    internal suspend fun CommandContext<CommandSender>.warpsWithoutAlias(input: CommandInput): List<String> {
        return WarpManager.list().map { it.name }
    }
}