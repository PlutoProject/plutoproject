package ink.pmc.member

import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import ink.pmc.member.api.IMemberService
import ink.pmc.member.bedrock.GeyserSimpleFloodgateApiReplacement
import ink.pmc.provider.ProviderService
import ink.pmc.framework.utils.isInDebugMode
import org.bukkit.plugin.java.JavaPlugin

lateinit var plugin: JavaPlugin

@Suppress("UNUSED")
class PaperPlugin : JavaPlugin() {

    override fun onEnable() {
        if (isInDebugMode()) {
            return
        }

        plugin = this
        serverLogger = logger

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
        memberService = PaperMemberService(ProviderService.defaultMongoDatabase)
        IMemberService.instance = memberService
    }

}