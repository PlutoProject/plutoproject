package ink.pmc.framework.entity

import ink.pmc.framework.concurrent.submitSync
import ink.pmc.framework.platform.isFoliaOrAsync
import kotlinx.coroutines.future.await
import org.bukkit.Location
import org.bukkit.entity.Entity

@Suppress("UNUSED")
fun Entity.ensureThreadSafe(block: Entity.() -> Unit) {
    if (isFoliaOrAsync) {
        this.submitSync {
            block()
        }
        return
    }
    block()
}

suspend fun Entity.teleportSuspend(location: Location) {
    teleportAsync(location).await()
}