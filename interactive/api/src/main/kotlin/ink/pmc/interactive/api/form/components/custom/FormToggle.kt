package ink.pmc.interactive.api.form.components.custom

import androidx.compose.runtime.Applier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import net.kyori.adventure.text.Component

@Composable
@Suppress("FunctionName")
fun FormToggle(
    text: Component = Component.empty(),
    defaultValue: Boolean = false
) {
    ComposeNode<FormToggleNode, Applier<Any>>(
        factory = {
            FormToggleNode(
                text = text,
                defaultValue = defaultValue
            )
        },
        update = {
            set(text) { this.text = it }
            set(defaultValue) { this.defaultValue = it }
        }
    )
}