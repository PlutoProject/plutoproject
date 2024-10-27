package ink.pmc.essentials.commands.warp

import ink.pmc.advkt.component.replace
import ink.pmc.essentials.COMMAND_WARP_NOT_EXISTED
import ink.pmc.essentials.api.warp.Warp
import ink.pmc.essentials.api.warp.WarpManager
import org.bukkit.command.CommandSender
import org.incendo.cloud.annotations.exception.ExceptionHandler
import org.incendo.cloud.annotations.parser.Parser
import org.incendo.cloud.annotations.suggestion.Suggestions
import org.incendo.cloud.context.CommandContext
import org.incendo.cloud.context.CommandInput
import org.incendo.cloud.parser.standard.StringParser
import kotlin.jvm.optionals.getOrNull

@Suppress("UNUSED", "UNUSED_PARAMETER")
object WarpCommons {
    @Suggestions("warps")
    suspend fun warps(context: CommandContext<CommandSender>, input: CommandInput): List<String> {
        return WarpManager.list().map {
            val name = it.name
            val alias = it.alias
            if (alias == null) name else "$name-$alias"
        }
    }

    @Suggestions("warps-without-alias")
    suspend fun warpsWithoutAlias(context: CommandContext<CommandSender>, input: CommandInput): List<String> {
        return WarpManager.list().map { it.name }
    }

    @Parser(name = "warp", suggestions = "warps")
    suspend fun warp(context: CommandContext<CommandSender>, input: CommandInput): Warp {
        val stringParser = StringParser.greedyStringParser<CommandSender>()
        val string = stringParser.parser().parse(context, input).parsedValue().getOrNull()
            ?: error("Unable to parse warp name")
        val name = parseWarpName(string)
        return WarpManager.get(name) ?: throw WarpNotExistedException(name)
    }

    @Parser(name = "warp-without-alias", suggestions = "warps-without-alias")
    suspend fun warpWithoutAlias(context: CommandContext<CommandSender>, input: CommandInput): Warp {
        val stringParser = StringParser.quotedStringParser<CommandSender>()
        val string = stringParser.parser().parse(context, input).parsedValue().getOrNull()
            ?: error("Unable to parse warp name")
        val name = parseWarpName(string)
        return WarpManager.get(name) ?: throw WarpNotExistedException(name)
    }

    @ExceptionHandler(WarpNotExistedException::class)
    fun CommandSender.warpNotExisted(exception: WarpNotExistedException) {
        sendMessage(COMMAND_WARP_NOT_EXISTED.replace("<name>", exception.name))
    }
}

fun parseWarpName(input: String): String {
    return input.substringBefore('-')
}

class WarpNotExistedException(val name: String) : Exception()