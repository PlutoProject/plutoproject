package ink.pmc.interactive.api

import androidx.compose.runtime.Composable
import org.bukkit.entity.Player

typealias ComposableFunction = @Composable () -> Unit

typealias Effect = (Player) -> Unit