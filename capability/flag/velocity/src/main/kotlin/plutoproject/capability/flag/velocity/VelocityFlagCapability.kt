package plutoproject.capability.flag.velocity

import com.velocitypowered.api.command.CommandSource
import plutoproject.capability.flag.api.FlagKey
import plutoproject.capability.flag.common.FlagCapability
import plutoproject.capability.legacycloudcommands.api.velocity.VelocityLegacyCloudCommands
import plutoproject.foundation.velocity.command.CloudCommandRegistration
import plutoproject.foundation.common.text.replace
import plutoproject.kernel.api.Capability
import plutoproject.kernel.api.ModuleContext
import plutoproject.kernel.api.Platform
import plutoproject.kernel.api.RuntimeModule
import plutoproject.kernel.api.getService
import plutoproject.kernel.api.velocity.VelocityModuleContext
import org.incendo.cloud.annotations.exception.ExceptionHandler
import org.incendo.cloud.parser.ParserDescriptor
import java.util.UUID

@Capability(
    id = "flag",
    platform = Platform.VELOCITY,
    requiredCapabilities = ["redis", "server_identifier", "legacy_cloud_commands"],
)
class VelocityFlagCapability : RuntimeModule {
    private val delegate = FlagCapability(allowWorldScope = false)
    private var commands: CloudCommandRegistration? = null

    override suspend fun onLoad(context: ModuleContext) = delegate.onLoad(context)

    override suspend fun onEnable(context: ModuleContext) {
        context as VelocityModuleContext
        val parser = context.services.getService<VelocityLegacyCloudCommands>().parser
        parser.manager().parserRegistry().apply {
            registerNamedParser("flag-key", ParserDescriptor.of(FlagKeyArgumentParser, FlagKey::class.java))
            registerNamedParser("flag-player", ParserDescriptor.of(PlayerArgumentParser, UUID::class.java))
        }
        commands = CloudCommandRegistration.register(parser, VelocityFlagCommand, FlagCommandExceptionHandlers)
    }

    override suspend fun onDisable(context: ModuleContext) {
        commands?.close()
        commands = null
    }
}

@Suppress("UNUSED")
internal object FlagCommandExceptionHandlers {
    @ExceptionHandler(FlagKeyNotFoundException::class)
    fun flagKeyNotFound(sender: CommandSource, exception: FlagKeyNotFoundException) {
        sender.sendMessage(COMMAND_FLAG_UNKNOWN_KEY.replace("<key>", exception.key))
    }

    @ExceptionHandler(PlayerNotFoundException::class)
    fun playerNotFound(sender: CommandSource, exception: PlayerNotFoundException) {
        sender.sendMessage(COMMAND_FLAG_PLAYER_NOT_FOUND.replace("<input>", exception.input))
    }
}
