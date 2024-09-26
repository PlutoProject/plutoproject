package ink.pmc.interactive.api.gui.form.components.custom

import androidx.compose.runtime.Applier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import net.kyori.adventure.text.Component

@Composable
@Suppress("FunctionName")
fun FormLabel(
    text: Component = Component.empty(),
) {
    ComposeNode<FormLabelNode, Applier<Any>>(
        factory = {
            FormLabelNode(
                text = text,
            )
        },
        update = {
            set(text) { this.text = it }
        }
    )
}