package plutoproject.capability.flag.paper

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.incendo.cloud.context.CommandContext
import org.incendo.cloud.context.CommandInput
import org.incendo.cloud.parser.ArgumentParseResult
import org.incendo.cloud.parser.ArgumentParser
import org.incendo.cloud.parser.standard.StringParser
import org.incendo.cloud.suggestion.BlockingSuggestionProvider
import org.incendo.cloud.suggestion.Suggestion
import plutoproject.capability.flag.api.FlagKey
import plutoproject.capability.flag.api.FlagRegistry
import plutoproject.kernel.api.koinGet
import java.util.UUID

/**
 * 解析 flag key 并直接返回注册表中对应的 [FlagKey]，同时提供已注册 key 的补全。
 */
internal object FlagKeyArgumentParser : ArgumentParser<CommandSender, FlagKey<*>>,
    BlockingSuggestionProvider<CommandSender> {
    private val stringParser = StringParser.stringParser<CommandSender>().parser()

    override fun parse(
        commandContext: CommandContext<CommandSender>,
        commandInput: CommandInput,
    ): ArgumentParseResult<FlagKey<*>> {
        val result = stringParser.parse(commandContext, commandInput)
        result.failure().orElse(null)?.let { return ArgumentParseResult.failure(it) }
        val raw = result.parsedValue().orElse(null) ?: return ArgumentParseResult.failure(
            IllegalArgumentException("Unable to parse flag key"),
        )
        val key = koinGet<FlagRegistry>().keys.firstOrNull { it.name == raw }
            ?: return ArgumentParseResult.failure(FlagKeyNotFoundException(raw))
        return ArgumentParseResult.success(key)
    }

    override fun suggestions(
        commandContext: CommandContext<CommandSender>,
        commandInput: CommandInput,
    ): List<Suggestion> = koinGet<FlagRegistry>().keys.map { Suggestion.suggestion(it.name) }
}

/**
 * 解析玩家参数：优先匹配在线玩家名（带补全），否则按原始 UUID 解析，支持离线玩家。
 */
internal object PlayerArgumentParser : ArgumentParser<CommandSender, UUID>,
    BlockingSuggestionProvider<CommandSender> {
    private val stringParser = StringParser.stringParser<CommandSender>().parser()

    override fun parse(
        commandContext: CommandContext<CommandSender>,
        commandInput: CommandInput,
    ): ArgumentParseResult<UUID> {
        val result = stringParser.parse(commandContext, commandInput)
        result.failure().orElse(null)?.let { return ArgumentParseResult.failure(it) }
        val raw = result.parsedValue().orElse(null) ?: return ArgumentParseResult.failure(
            IllegalArgumentException("Unable to parse player"),
        )
        val uuid = resolvePlayerToken(raw)
            ?: return ArgumentParseResult.failure(PlayerNotFoundException(raw))
        return ArgumentParseResult.success(uuid)
    }

    override fun suggestions(
        commandContext: CommandContext<CommandSender>,
        commandInput: CommandInput,
    ): List<Suggestion> = Bukkit.getServer().onlinePlayers.map { Suggestion.suggestion(it.name) }
}

internal class FlagKeyNotFoundException(val key: String) : RuntimeException()

internal class PlayerNotFoundException(val input: String) : RuntimeException()
