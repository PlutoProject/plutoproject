package ink.pmc.member

import com.github.shynixn.mccoroutine.velocity.SuspendingPluginContainer
import com.github.shynixn.mccoroutine.velocity.registerSuspend
import com.google.inject.Inject
import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent
import com.velocitypowered.api.plugin.PluginContainer
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import ink.pmc.member.api.IMemberService
import ink.pmc.member.bedrock.GeyserPlayerLinkReplacement
import ink.pmc.member.bedrock.GeyserSimpleFloodgateApiReplacement
import ink.pmc.member.commands.MemberCommand
import ink.pmc.rpc.api.RpcServer
import ink.pmc.utils.command.init
import ink.pmc.utils.platform.proxy
import ink.pmc.utils.platform.saveDefaultConfig
import org.incendo.cloud.SenderMapper
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.velocity.VelocityCommandManager
import java.io.File
import java.nio.file.Path
import java.util.logging.Logger

lateinit var pluginContainer: PluginContainer
lateinit var commandManager: VelocityCommandManager<CommandSource>

@Suppress("UNUSED", "UNUSED_PARAMETER")
class VelocityPlugin @Inject constructor(suspendingPluginContainer: SuspendingPluginContainer) {

    init {
        suspendingPluginContainer.initialize(this)
    }

    @Inject
    fun memberPluginVelocity(server: ProxyServer, logger: Logger, @DataDirectory dataDirectoryPath: Path) {
        serverLogger = logger
        dataDir = dataDirectoryPath.toFile()

        createDataDir()
        configFile = File(dataDir, "config.toml")

        if (!configFile.exists()) {
            saveDefaultConfig(VelocityPlugin::class.java, configFile)
        }

        initVelocityService()

        RpcServer.apply {
            addService((memberService as VelocityMemberService).rpcService)
        }
    }

    @Subscribe
    fun proxyInitializeEvent(event: ProxyInitializeEvent) {
        pluginContainer = proxy.pluginManager.getPlugin("common-member").get()

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

    private fun initVelocityService() {
        initService()
        memberService = VelocityMemberService(database)
        IMemberService.instance = memberService
    }

}