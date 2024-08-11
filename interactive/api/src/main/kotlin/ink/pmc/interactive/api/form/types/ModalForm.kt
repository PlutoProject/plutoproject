package ink.pmc.interactive.api.form.types

import androidx.compose.runtime.Applier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import ink.pmc.interactive.api.ComposableFunction
import ink.pmc.interactive.api.form.FormResultHandler
import net.kyori.adventure.text.Component
import org.geysermc.cumulus.form.ModalForm
import org.geysermc.cumulus.response.ModalFormResponse

@Composable
@Suppress("FunctionName")
fun ModalForm(
    title: Component = Component.empty(),
    content: Component = Component.empty(),
    button1: Component = Component.empty(),
    button2: Component = Component.empty(),
    resultHandler: FormResultHandler<ModalForm, ModalFormResponse> = { _, _ -> },
    contents: ComposableFunction
) {
    ComposeNode<ModalFormNode, Applier<Any>>(
        factory = {
            ModalFormNode(
                title = title,
                content = content,
                button1 = button1,
                button2 = button2,
                resultHandler = resultHandler
            )
        },
        update = {
            set(title) { this.title = it }
            set(content) { this.content = it }
            set(button1) { this.button1 = it }
            set(button2) { this.button2 = it }
            set(resultHandler) { this.resultHandler = it }
        },
        content = contents
    )
}