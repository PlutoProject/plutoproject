package ink.pmc.framework.interactive.canvas

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import ink.pmc.framework.concurrent.submitSync
import ink.pmc.framework.frameworkPaper
import ink.pmc.framework.interactive.LocalGuiScope
import ink.pmc.framework.interactive.LocalPlayer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.wesjd.anvilgui.AnvilGUI
import net.wesjd.anvilgui.AnvilGUI.ResponseAction
import net.wesjd.anvilgui.AnvilGUI.StateSnapshot
import org.bukkit.inventory.ItemStack

typealias CloseListener = (StateSnapshot) -> Unit
typealias ClickListener = (Int, StateSnapshot) -> List<ResponseAction>

@Composable
@Suppress("FunctionName")
fun Anvil(
    title: Component,
    left: ItemStack? = null,
    right: ItemStack? = null,
    output: ItemStack? = null,
    text: String = "",
    onClose: CloseListener = {},
    onClick: ClickListener = { _, _ -> emptyList() },
) {
    val player = LocalPlayer.current
    val scope = LocalGuiScope.current
    var textState by rememberSaveable { mutableStateOf(text) }
    remember(title, left, right, output) {
        AnvilGUI.Builder()
            .plugin(frameworkPaper)
            .jsonTitle(GsonComponentSerializer.gson().serialize(title))
            .text(textState)
            .apply {
                if (left != null) itemLeft(left)
                if (right != null) itemRight(right)
                if (output != null) itemOutput(output)
            }
            .onClose {
                textState = it.text
                onClose(it)
            }
            .onClick { i, s ->
                textState = s.text
                onClick(i, s)
            }
            .also {
                scope.setPendingRefreshIfNeeded(true)
                submitSync { it.open(player) }
            }
    }
}