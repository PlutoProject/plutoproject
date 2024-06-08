package ink.pmc.utils

import ink.pmc.utils.bedrock.floodgateApi
import ink.pmc.utils.bedrock.floodgateApiClass
import ink.pmc.utils.bedrock.floodgateSupport
import ink.pmc.utils.bedrock.isFloodgatePlayer
import ink.pmc.utils.jvm.byteBuddy
import ink.pmc.utils.platform.*
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import java.util.concurrent.Executor

private fun checkFolia(): Boolean {
    try {
        Class.forName("io.papermc.paper.threadedregions.RegionizedServer")
        return true
    } catch (e: ClassNotFoundException) {
        return false
    }
}

@Suppress("UNUSED")
class PaperPlugin : JavaPlugin() {

    override fun onLoad() {
        byteBuddy // 初始化
    }

    override fun onEnable() {
        isFolia = checkFolia()
        paperThread = Thread.currentThread()
        paper = server
        paperUtilsPlugin = this

        // Folia 上复写了 EventLoop 的 tell 方法，尝试直接提交任务会丢出 UnsupportedOperationException。
        if (!isFolia) {
            /*
            * 反射获取 DedicatedServer。
            * 由于 DedicatedServer 本身继承了 Executor，这里我们把它当作 Executor 来用。
            * */
            val craftServerClass = paper.javaClass
            val craftServer = craftServerClass.cast(paper)
            val method = craftServer.javaClass.getDeclaredMethod("getServer")
            val dedicatedServer = method.invoke(craftServer)

            serverExecutor = dedicatedServer as Executor
        }

        if (floodgateSupport()) {
            floodgateApiClass = Class.forName("org.geysermc.floodgate.api.FloodgateApi")
            floodgateApi = floodgateApiClass.getDeclaredMethod("getInstance").invoke(null)
            isFloodgatePlayer = floodgateApiClass.getDeclaredMethod("isFloodgatePlayer", UUID::class.java)
        }
    }

}