package ink.pmc.utils.world

import ink.pmc.utils.concurrent.submitSync
import ink.pmc.utils.platform.isFoliaOrAsync
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