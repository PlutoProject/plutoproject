package ink.pmc.interactive.api

import androidx.compose.runtime.Applier
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.CoroutineScope
import org.bukkit.entity.Player

@Suppress("UNUSED")
val LocalGuiScope: ProvidableCompositionLocal<GuiScope<*>> =
    staticCompositionLocalOf { error("InteractiveScope not provided") }

@Suppress("UNUSED")
val LocalPlayer: ProvidableCompositionLocal<Player> =
    staticCompositionLocalOf { error("Player not provided") }

@Suppress("UNUSED")
interface GuiScope<T> : CoroutineScope {

    val isDisposed: Boolean
    val owner: Player
    val rootNode: T
    val nodeApplier: Applier<T>

    fun dispose()

}