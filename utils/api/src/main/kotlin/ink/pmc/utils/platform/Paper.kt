package ink.pmc.utils.platform

import net.minecraft.server.MinecraftServer
import org.bukkit.Server
import org.bukkit.craftbukkit.CraftServer
import org.bukkit.plugin.java.JavaPlugin
import java.util.concurrent.Executor

lateinit var paperThread: Thread
lateinit var paper: Server
lateinit var paperUtilsPlugin: JavaPlugin
lateinit var serverExecutor: Executor

@Suppress("UNUSED")
inline val Thread.isServerThread: Boolean
    get() = this == paperThread

inline val isAsync: Boolean
    get() = Thread.currentThread() != paperThread

inline val isFoliaOrAsync: Boolean
    get() = isFolia || isAsync

inline val Server.internal: MinecraftServer
    get() = (this as CraftServer).server