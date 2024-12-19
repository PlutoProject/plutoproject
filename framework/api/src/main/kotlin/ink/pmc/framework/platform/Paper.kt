package ink.pmc.framework.platform

import net.minecraft.server.MinecraftServer
import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.craftbukkit.CraftServer
import java.util.concurrent.Executor

lateinit var paperThread: Thread

val isFolia = try {
    Class.forName("io.papermc.paper.threadedregions.RegionizedServer")
    true
} catch (e: Exception) {
    false
}

inline val paper: Server
    get() = Bukkit.getServer()

inline val Thread.isPaperThread: Boolean
    get() = this == paperThread

inline val isAsync: Boolean
    get() = Thread.currentThread() != paperThread

inline val isFoliaOrAsync: Boolean
    get() = isFolia || isAsync

inline val Server.internal: MinecraftServer
    get() = (this as CraftServer).server

inline val Server.executor: Executor
    get() = internal