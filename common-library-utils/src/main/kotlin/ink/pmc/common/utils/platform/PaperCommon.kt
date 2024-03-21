package ink.pmc.common.utils.platform

import org.bukkit.Location
import org.bukkit.entity.Player

lateinit var serverThread: Thread

@Suppress("UNUSED")
val Thread.isServerThread: Boolean
    get() = this == serverThread

val isFoliaOrAsync: Boolean
    get() = isFolia || Thread.currentThread() != serverThread

@Suppress("UNUSED")
fun Player.threadSafeTeleport(location: Location) {
    if (isFoliaOrAsync) {
        this.teleportAsync(location)
        return
    }

    this.teleport(location)
}