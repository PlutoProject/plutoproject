package ink.pmc.interactive.api.gui.form.components.simple

import androidx.compose.runtime.Applier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import net.kyori.adventure.text.Component
import org.geysermc.cumulus.form.SimpleForm
import org.geysermc.cumulus.response.SimpleFormResponse
import org.geysermc.cumulus.util.FormImage

typealias SimpleFormButtonHandler = (SimpleForm, SimpleFormResponse) -> Unit

@Composable
@Suppress("FunctionName")
fun FormButton(
    text: Component = Component.empty(),
    image: FormImage? = null,
    onClick: SimpleFormButtonHandler = { _, _ -> }
) {
    ComposeNode<FormButtonNode, Applier<Any>>(
        factory = {
            FormButtonNode(
                text = text,
                image = image,
                onClick = onClick
            )
        },
        update = {
            set(text) { this.text = it }
            set(image) { this.image = it }
            set(onClick) { this.onClick = onClick }
        }
    )
}