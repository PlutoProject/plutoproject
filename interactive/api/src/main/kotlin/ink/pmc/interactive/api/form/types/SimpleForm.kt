package ink.pmc.interactive.api.form.types

import androidx.compose.runtime.Applier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import ink.pmc.interactive.api.ComposableFunction
import ink.pmc.interactive.api.form.FormResultHandler
import net.kyori.adventure.text.Component
import org.geysermc.cumulus.form.SimpleForm
import org.geysermc.cumulus.response.SimpleFormResponse

@Composable
@Suppress("FunctionName")
fun SimpleForm(
    title: Component = Component.empty(),
    content: Component = Component.empty(),
    resultHandler: FormResultHandler<SimpleForm, SimpleFormResponse> = { _, _ -> },
    contents: ComposableFunction
) {
    ComposeNode<SimpleFormNode, Applier<SimpleFormNode>>(
        factory = {
            SimpleFormNode(
                title = title,
                content = content,
                resultHandler = resultHandler
            )
        },
        update = {
            set(title) { this.title = it }
            set(content) { this.content = it }
            set(resultHandler) { this.resultHandler = it }
        },
        content = contents
    )
}