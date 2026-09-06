package plutoproject.capability.flag.paper

import plutoproject.capability.flag.common.FlagCapability
import plutoproject.capability.legacycloudcommands.api.paper.PaperLegacyCloudCommands
import plutoproject.capability.flag.api.FlagKey
import plutoproject.foundation.common.text.replace
import plutoproject.foundation.paper.command.CloudCommandRegistration
import plutoproject.kernel.api.Capability
import plutoproject.kernel.api.ModuleContext
import plutoproject.kernel.api.Platform
import plutoproject.kernel.api.RuntimeModule
import plutoproject.kernel.api.getService
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.incendo.cloud.annotations.exception.ExceptionHandler
import org.incendo.cloud.context.CommandContext
import org.incendo.cloud.context.CommandInput
import org.incendo.cloud.parser.ParserDescriptor
import org.incendo.cloud.suggestion.BlockingSuggestionProvider
import org.incendo.cloud.suggestion.Suggestion
import java.util.UUID

internal object WorldNameSuggestionProvider : BlockingSuggestionProvider<CommandSender> {
    override fun suggestions(
        commandContext: CommandContext<CommandSender>,
        commandInput: CommandInput,
    ): List<Suggestion> = Bukkit.getWorlds().map { Suggestion.suggestion(it.name) }
}

@Capability(
    id = "flag",
    platform = Platform.PAPER,
    requiredCapabilities = ["redis", "server_identifier", "legacy_cloud_commands"],
)
class PaperFlagCapability : RuntimeModule {
    private val delegate = FlagCapability(allowWorldScope = true)
    private var commands: CloudCommandRegistration? = null

    override suspend fun onLoad(context: ModuleContext) = delegate.onLoad(context)

    override suspend fun onEnable(context: ModuleContext) {
        val parser = context.services.getService<PaperLegacyCloudCommands>().parser
        parser.manager().parserRegistry().apply {
            registerNamedParser("flag-key", ParserDescriptor.of(FlagKeyArgumentParser, FlagKey::class.java))
            registerNamedParser("flag-player", ParserDescriptor.of(PlayerArgumentParser, UUID::class.java))
            registerSuggestionProvider("flag-world", WorldNameSuggestionProvider)
        }
        commands = CloudCommandRegistration.register(parser, PaperFlagCommand, FlagCommandExceptionHandlers)
    }

    override suspend fun onDisable(context: ModuleContext) {
        commands?.close()
        commands = null
    }
}

@Suppress("UNUSED")
internal object FlagCommandExceptionHandlers {
    @ExceptionHandler(FlagKeyNotFoundException::class)
    fun flagKeyNotFound(sender: CommandSender, exception: FlagKeyNotFoundException) {
        sender.sendMessage(COMMAND_FLAG_UNKNOWN_KEY.replace("<key>", exception.key))
    }

    @ExceptionHandler(PlayerNotFoundException::class)
    fun playerNotFound(sender: CommandSender, exception: PlayerNotFoundException) {
        sender.sendMessage(COMMAND_FLAG_PLAYER_NOT_FOUND.replace("<input>", exception.input))
    }
}
