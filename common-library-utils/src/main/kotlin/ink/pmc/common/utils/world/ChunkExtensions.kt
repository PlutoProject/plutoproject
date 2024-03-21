package ink.pmc.common.utils.world

import ink.pmc.common.utils.concurrent.submitSync
import ink.pmc.common.utils.platform.isFoliaOrAsync
import org.bukkit.Chunk

@Suppress("UNUSED")
fun Chunk.ensureThreadSafe(block: Chunk.() -> Unit) {
    if (isFoliaOrAsync) {
        this.submitSync {
            block()
        }
        return
    }

    block()
}