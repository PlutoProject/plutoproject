package ink.pmc.bedrockadaptive

import com.github.shynixn.mccoroutine.velocity.SuspendingPluginContainer
import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.network.ProtocolVersion
import com.velocitypowered.api.plugin.PluginContainer
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import dev.simplix.protocolize.api.Protocolize
import ink.pmc.bedrockadaptive.velocity.*
import ink.pmc.bedrockadaptive.velocity.replacements.BedrockColorSerializerReplacement
import ink.pmc.bedrockadaptive.velocity.replacements.GeyserAttackIndicatorReplacement
import ink.pmc.bedrockadaptive.velocity.replacements.SystemChatPacketDecodeReplacement
import ink.pmc.bedrockadaptive.velocity.replacements.TitlePacketDecodeReplacement
import ink.pmc.utils.platform.proxy
import java.nio.file.Path
import java.util.logging.Logger

lateinit var serverLogger: Logger
lateinit var pluginContainer: PluginContainer
val protocolVersion = ProtocolVersion.MINECRAFT_1_20_3

@Suppress("UNUSED", "UNUSED_PARAMETER")
class VelocityPlugin @Inject constructor(suspendingPluginContainer: SuspendingPluginContainer) {

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
        pluginContainer = proxy.pluginManager.getPlugin("bedrock-adaptive").get()

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
    }

}