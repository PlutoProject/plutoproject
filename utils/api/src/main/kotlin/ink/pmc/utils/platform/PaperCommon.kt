package ink.pmc.utils.platform

import org.bukkit.Location
import org.bukkit.Server
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

lateinit var paperThread: Thread
lateinit var paper: Server
lateinit var paperUtilsPlugin: JavaPlugin

@Suppress("UNUSED")
val Thread.isServerThread: Boolean
    get() = this == paperThread

val isFoliaOrAsync: Boolean
    get() = isFolia || Thread.currentThread() != paperThread

@Suppress("UNUSED")
fun Player.threadSafeTeleport(location: Location) {
    if (isFoliaOrAsync) {
        this.teleportAsync(location)
        return
    }

    this.teleport(location)
}