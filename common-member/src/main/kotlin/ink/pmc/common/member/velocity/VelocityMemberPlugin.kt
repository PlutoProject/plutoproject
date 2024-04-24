package ink.pmc.common.member.velocity

import com.github.shynixn.mccoroutine.velocity.SuspendingPluginContainer
import com.github.shynixn.mccoroutine.velocity.registerSuspend
import com.google.inject.Inject
import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent
import com.velocitypowered.api.plugin.Dependency
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.plugin.PluginContainer
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import ink.pmc.common.member.*
import ink.pmc.common.member.bedrock.GeyserPlayerLinkReplacement
import ink.pmc.common.member.bedrock.GeyserSimpleFloodgateApiReplacement
import ink.pmc.common.member.commands.MemberCommand
import ink.pmc.common.utils.command.init
import ink.pmc.common.utils.platform.proxy
import ink.pmc.common.utils.platform.saveDefaultConfig
import org.incendo.cloud.SenderMapper
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.velocity.VelocityCommandManager
import java.io.File
import java.nio.file.Path
import java.util.logging.Logger

lateinit var pluginContainer: PluginContainer
lateinit var commandManager: VelocityCommandManager<CommandSource>

@Plugin(
    id = "common-member",
    name = "common-member",
    version = "1.0.2",
    dependencies = [
        Dependency(id = "common-dependency-loader-velocity"),
        Dependency(id = "common-utils"),
        Dependency(id = "floodgate", optional = true)
    ]
)
@Suppress("UNUSED", "UNUSED_PARAMETER")
class VelocityMemberPlugin @Inject constructor(suspendingPluginContainer: SuspendingPluginContainer) {

    init {
        suspendingPluginContainer.initialize(this)
    }

    @Inject
    fun memberPluginVelocity(server: ProxyServer, logger: Logger, @DataDirectory dataDirectoryPath: Path) {
        serverLogger = logger
        dataDir = dataDirectoryPath.toFile()
    }

    @Subscribe
    fun proxyInitializeEvent(event: ProxyInitializeEvent) {
        pluginContainer = proxy.pluginManager.getPlugin("common-member").get()

        createDataDir()
        configFile = File(dataDir, "config.toml")

        if (!configFile.exists()) {
            saveDefaultConfig(VelocityMemberPlugin::class.java, configFile)
        }

        initMemberService()
        GeyserPlayerLinkReplacement.init()
        GeyserSimpleFloodgateApiReplacement.init()

        commandManager = VelocityCommandManager(
            pluginContainer,
            proxy,
            ExecutionCoordinator.asyncCoordinator(),
            SenderMapper.identity()
        )

        commandManager.init(MemberCommand)
        proxy.eventManager.registerSuspend(this, VelocityPlayerListener)
        disabled = false
    }

    @Subscribe
    fun proxyShutdownEvent(event: ProxyShutdownEvent) {
        safeDisable()
        disabled = true
    }

}