package ink.pmc.visual

import com.fren_gor.ultimateAdvancementAPI.AdvancementMain
import com.fren_gor.ultimateAdvancementAPI.UltimateAdvancementAPI
import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin

internal lateinit var uaMain: AdvancementMain
internal lateinit var uaApi: UltimateAdvancementAPI

@Suppress("UNUSED")
class PaperPlugin : SuspendingJavaPlugin() {

    override suspend fun onLoadAsync() {
        uaMain = AdvancementMain(this)
        uaMain.load()
    }

    override suspend fun onEnableAsync() {
        uaMain.enableInMemory()
        uaApi = UltimateAdvancementAPI.getInstance(this)
    }

    override suspend fun onDisableAsync() {
        uaMain.disable()
    }

}