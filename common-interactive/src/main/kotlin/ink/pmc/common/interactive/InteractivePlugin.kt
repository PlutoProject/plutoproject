package ink.pmc.common.interactive

import org.bukkit.plugin.java.JavaPlugin

var disabled = false
lateinit var plugin: JavaPlugin

@Suppress("UNUSED")
class InteractivePlugin : JavaPlugin() {

    override fun onEnable() {
        plugin = this
        disabled = false
    }

    override fun onDisable() {
        disabled = true
    }

}