package ink.pmc.common.member.paper

import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import ink.pmc.common.member.*
import ink.pmc.common.member.api.session.ISessionService
import ink.pmc.common.member.session.PaperSessionService
import ink.pmc.common.utils.isInDebugMode
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

lateinit var plugin: JavaPlugin

@Suppress("UNUSED")
class PaperMemberPlugin : JavaPlugin() {

    override fun onEnable() {
        if (isInDebugMode()) {
            return
        }

        plugin = this
        serverLogger = logger
        dataDir = dataFolder

        createDataDir()
        configFile = File(dataFolder, "config.toml")

        if (!configFile.exists()) {
            saveResource("config.toml", false)
        }

        initMemberService()
        sessionService = PaperSessionService()
        sessionService.init()
        ISessionService.instance = sessionService

        server.pluginManager.registerSuspendingEvents(PaperPlayerListener, this)

        disabled = false
    }

    override fun onDisable() {
        if (isInDebugMode()) {
            return
        }

        safeDisable()
        disabled = true
    }

}