package ink.pmc.utils.entity

import ink.pmc.utils.concurrent.submitSync
import ink.pmc.utils.platform.isFoliaOrAsync
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