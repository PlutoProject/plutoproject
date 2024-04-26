package ink.pmc.common.bedrockadaptive

import com.github.shynixn.mccoroutine.velocity.SuspendingPluginContainer
import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent
import com.velocitypowered.api.network.ProtocolVersion
import com.velocitypowered.api.plugin.Dependency
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.plugin.PluginContainer
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import dev.simplix.protocolize.api.Protocolize
import ink.pmc.common.bedrockadaptive.velocity.*
import ink.pmc.common.bedrockadaptive.velocity.replacements.BedrockColorSerializerReplacement
import ink.pmc.common.bedrockadaptive.velocity.replacements.GeyserAttackIndicatorReplacement
import ink.pmc.common.bedrockadaptive.velocity.replacements.SystemChatPacketDecodeReplacement
import ink.pmc.common.bedrockadaptive.velocity.replacements.TitlePacketDecodeReplacement
import ink.pmc.common.utils.platform.proxy
import java.nio.file.Path
import java.util.logging.Logger

lateinit var serverLogger: Logger
lateinit var pluginContainer: PluginContainer
val protocolVersion = ProtocolVersion.MINECRAFT_1_20_3

@Plugin(
    id = "common-bedrock-adaptive",
    name = "common-bedrock-adaptive",
    version = "1.0.2",
    dependencies = [
        Dependency(id = "common-dependency-loader-velocity"),
        Dependency(id = "common-utils"),
        Dependency(id = "common-member"),
        Dependency(id = "protocolize"),
        Dependency(id = "geyser"),
        Dependency(id = "floodgate")
    ]
)
@Suppress("UNUSED", "UNUSED_PARAMETER")
class VelocityBedrockAdaptivePlugin @Inject constructor(suspendingPluginContainer: SuspendingPluginContainer) {

    init {
        suspendingPluginContainer.initialize(this)

        TitlePacketDecodeReplacement.init()
        SystemChatPacketDecodeReplacement.init()
        GeyserAttackIndicatorReplacement.init()
        BedrockColorSerializerReplacement.init()
    }

    @Inject
    fun bedrockAdaptivePluginVelocity(server: ProxyServer, logger: Logger, @DataDirectory dataDirectoryPath: Path) {
        serverLogger = logger
    }

    @Subscribe
    fun proxyInitializeEvent(event: ProxyInitializeEvent) {
        pluginContainer = proxy.pluginManager.getPlugin("common-bedrock-adaptive").get()

        Protocolize.listenerProvider().registerListener(SystemChatPacketListener)
        Protocolize.listenerProvider().registerListener(TitleTextPacketListener)
        Protocolize.listenerProvider().registerListener(TitleSubtitlePacketListener)
        Protocolize.listenerProvider().registerListener(TitleActionbarPacketListener)
        /*
        * HeaderAndFooter 没有实现解码，无法监听。
        * 实际上基岩版也没有这玩意，所以就先不注册了。
        * */
        // Protocolize.listenerProvider().registerListener(HeaderAndFooterPacketListener)
        Protocolize.listenerProvider().registerListener(BossBarPacketListener)

        disabled = false
    }

    @Subscribe
    fun proxyShutdownEvent(event: ProxyShutdownEvent) {
        disabled = true
    }

}