package ink.pmc.common.utils

import ink.pmc.common.utils.platform.isFolia
import ink.pmc.common.utils.platform.serverThread
import org.bukkit.plugin.java.JavaPlugin

private fun checkFolia(): Boolean {
    try {
        Class.forName("io.papermc.paper.threadedregions.RegionizedServer")
        return true
    } catch (e: ClassNotFoundException) {
        return false
    }
}

@Suppress("UNUSED")
class PaperUtilsPlugin : JavaPlugin() {

    override fun onEnable() {
        isFolia = checkFolia()
        serverThread = Thread.currentThread()
    }

}