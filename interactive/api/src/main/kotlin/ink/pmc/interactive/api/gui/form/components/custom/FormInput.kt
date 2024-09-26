package ink.pmc.interactive.api.gui.form.components.custom

import androidx.compose.runtime.Applier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import net.kyori.adventure.text.Component

@Composable
@Suppress("FunctionName")
fun FormInput(
    text: Component = Component.empty(),
    placeholder: String = "",
    defaultText: String = ""
) {
    ComposeNode<FormInputNode, Applier<Any>>(
        factory = {
            FormInputNode(
                text = text,
                placeholder = placeholder,
                defaultText = defaultText
            )
        },
        update = {
            set(text) { this.text = it }
            set(placeholder) { this.placeholder = it }
            set(defaultText) { this.defaultText = it }
        }
    )
}