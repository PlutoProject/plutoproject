package ink.pmc.menu

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import ink.pmc.framework.utils.command.annotationParser
import ink.pmc.framework.utils.command.commandManager
import ink.pmc.menu.commands.MenuCommand
import net.milkbowl.vault.economy.Economy
import org.bukkit.plugin.java.JavaPlugin

lateinit var plugin: JavaPlugin
lateinit var economy: Economy

@Suppress("UNUSED")
class PaperPlugin : SuspendingJavaPlugin() {
    override suspend fun onEnableAsync() {
        plugin = this
        commandManager().annotationParser().apply {
            parse(MenuCommand)
        }
        server.servicesManager.getRegistration(Economy::class.java)?.provider?.also { economy = it }
    }
}