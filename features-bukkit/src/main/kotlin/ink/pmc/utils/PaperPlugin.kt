package ink.pmc.utils

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import ink.pmc.utils.hook.initPaperHooks
import ink.pmc.utils.jvm.byteBuddy
import ink.pmc.utils.platform.*
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
class PaperPlugin : SuspendingJavaPlugin() {

    override fun onLoad() {
        byteBuddy // 初始化
    }

    override suspend fun onEnableAsync() {
        isFolia = checkFolia()
        paperThread = Thread.currentThread()
        paper = server
        paperUtilsPlugin = this
        utilsLogger = logger

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

        this.server.messenger.registerOutgoingPluginChannel(this, "BungeeCord")
        initPaperHooks()
    }

}