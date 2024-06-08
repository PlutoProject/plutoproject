package ink.pmc.member

import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import ink.pmc.member.api.IMemberService
import ink.pmc.member.bedrock.GeyserSimpleFloodgateApiReplacement
import ink.pmc.utils.isInDebugMode
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

lateinit var plugin: JavaPlugin

@Suppress("UNUSED")
class PaperPlugin : JavaPlugin() {

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

        initPaperService()
        GeyserSimpleFloodgateApiReplacement.init()

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

    private fun initPaperService() {
        initService()
        memberService = PaperMemberService(database)
        IMemberService.instance = memberService
    }

}