package ink.pmc.utils

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import ink.pmc.utils.hook.initPaperHooks
import ink.pmc.utils.jvm.byteBuddy
import ink.pmc.utils.platform.paperThread
import ink.pmc.utils.platform.utilsLogger

@Suppress("UNUSED")
class PaperPlugin : SuspendingJavaPlugin() {
    override fun onLoad() {
        byteBuddy // 初始化
    }

    override suspend fun onEnableAsync() {
        paperThread = Thread.currentThread()
        utilsLogger = logger
        this.server.messenger.registerOutgoingPluginChannel(this, "BungeeCord")
        initPaperHooks()
    }
}