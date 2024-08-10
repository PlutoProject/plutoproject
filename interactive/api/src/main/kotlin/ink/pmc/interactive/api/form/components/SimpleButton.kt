package ink.pmc.interactive.api.form.components

import androidx.compose.runtime.Applier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import net.kyori.adventure.text.Component
import org.geysermc.cumulus.util.FormImage

@Composable
@Suppress("FunctionName")
fun SimpleButton(
    text: Component = Component.empty(),
    image: FormImage? = null,
) {
    ComposeNode<SimpleButtonNode, Applier<Any>>(
        factory = {
            SimpleButtonNode(
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