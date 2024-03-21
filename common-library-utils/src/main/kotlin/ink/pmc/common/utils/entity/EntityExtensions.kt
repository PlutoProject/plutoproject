package ink.pmc.common.utils.entity

import ink.pmc.common.utils.concurrent.submitSync
import ink.pmc.common.utils.platform.isFoliaOrAsync
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