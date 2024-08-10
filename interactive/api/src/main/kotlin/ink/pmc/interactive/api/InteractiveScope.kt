package ink.pmc.interactive.api

import androidx.compose.runtime.Applier
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.CoroutineScope
import org.bukkit.entity.Player

@Suppress("UNUSED")
val LocalInteractiveScope: ProvidableCompositionLocal<InteractiveScope<*>> =
    staticCompositionLocalOf { error("InteractiveScope not provided") }

@Suppress("UNUSED")
interface InteractiveScope<T> : CoroutineScope {

    val isDisposed: Boolean
    val owner: Player
    val rootNode: T
    val nodeApplier: Applier<T>

    fun dispose()

}