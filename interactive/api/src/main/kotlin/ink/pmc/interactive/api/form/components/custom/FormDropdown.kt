package ink.pmc.interactive.api.form.components.custom

import androidx.compose.runtime.Applier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import net.kyori.adventure.text.Component

@Composable
@Suppress("FunctionName")
fun FormDropdown(
    text: Component = Component.empty(),
    options: Collection<String>,
    defaultOption: Int = 0,
) {
    ComposeNode<FormDropdownNode, Applier<Any>>(
        factory = {
            FormDropdownNode(
                text = text,
                options = options,
                defaultOption = defaultOption
            )
        },
        update = {
            set(text) { this.text = it }
            set(options) { this.options = it }
            set(defaultOption) { this.defaultOption = it }
        }
    )
}