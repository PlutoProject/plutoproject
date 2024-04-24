package ink.pmc.common.bedrockadaptive

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import org.bukkit.plugin.java.JavaPlugin

lateinit var plugin: JavaPlugin
lateinit var protocolManager: ProtocolManager
var disabled = true

@Suppress("UNUSED")
class BedrockAdaptivePlugin : JavaPlugin() {

    override fun onEnable() {
        plugin = this
        disabled = false
        protocolManager = ProtocolLibrary.getProtocolManager()
    }

    override fun onDisable() {
        disabled = true
    }

}