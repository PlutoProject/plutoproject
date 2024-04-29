package ink.pmc.common.bedrockadaptive

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import ink.pmc.common.utils.bedrock.floodgateSupport
import org.bukkit.plugin.java.JavaPlugin

lateinit var plugin: JavaPlugin
lateinit var protocolManager: ProtocolManager
var disabled = true

@Suppress("UNUSED")
class PaperBedrockAdaptivePlugin : JavaPlugin() {

    override fun onEnable() {
        if (!floodgateSupport()) {
            return
        }

        plugin = this
        disabled = false
        protocolManager = ProtocolLibrary.getProtocolManager()
    }

    override fun onDisable() {
        disabled = true
    }

}