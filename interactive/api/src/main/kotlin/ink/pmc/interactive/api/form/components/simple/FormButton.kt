package ink.pmc.interactive.api.form.components.simple

import androidx.compose.runtime.Applier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import net.kyori.adventure.text.Component
import org.geysermc.cumulus.util.FormImage

@Composable
@Suppress("FunctionName")
fun FormButton(
    text: Component = Component.empty(),
    image: FormImage? = null,
) {
    ComposeNode<FormButtonNode, Applier<Any>>(
        factory = {
            FormButtonNode(
                text = text,
                image = image
            )
        },
        update = {
            set(text) { this.text = it }
            set(image) { this.image = it }
        }
    )
}