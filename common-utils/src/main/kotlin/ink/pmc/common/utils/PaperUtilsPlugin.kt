package ink.pmc.common.utils

import ink.pmc.common.utils.platform.isFolia
import ink.pmc.common.utils.platform.serverExecutor
import ink.pmc.common.utils.platform.serverThread
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
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
class PaperUtilsPlugin : JavaPlugin() {

    override fun onEnable() {
        isFolia = checkFolia()
        serverThread = Thread.currentThread()

        // Folia 上复写了 EventLoop 的 tell 方法，尝试直接提交任务会丢出 UnsupportedOperationException。
        if (!isFolia) {
            /*
            * 反射获取 DedicatedServer。
            * 由于 DedicatedServer 本身继承了 Executor，这里我们把它当作 Executor 来用。
            * */
            val serverClassLoader = server.javaClass.classLoader
            val craftServerClass = Class.forName("org.bukkit.craftbukkit.v1_20_R3.CraftServer", true, serverClassLoader)
            val server = Bukkit.getServer()
            val craftServer = craftServerClass.cast(server)
            val method = craftServer.javaClass.getDeclaredMethod("getServer")
            val dedicatedServer = method.invoke(craftServer)

            serverExecutor = dedicatedServer as Executor
        }
    }

}