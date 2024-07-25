package ink.pmc.utils.platform

import ink.pmc.utils.concurrent.submitSync
import ink.pmc.utils.concurrent.sync
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

val isAsync: Boolean
    get() = Thread.currentThread() != paperThread

val isFoliaOrAsync: Boolean
    get() = isFolia || isAsync

@Suppress("UNUSED")
fun Player.threadSafeTeleport(location: Location) {
    if (isFolia) {
        teleportAsync(location)
        return
    }

    if (isAsync) {
        submitSync {
            teleport(location)
        }
        return
    }

    teleport(location)
}