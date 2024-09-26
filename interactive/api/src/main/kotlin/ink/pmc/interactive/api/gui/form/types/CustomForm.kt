package ink.pmc.interactive.api.gui.form.types

import androidx.compose.runtime.Applier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import ink.pmc.interactive.api.ComposableFunction
import ink.pmc.interactive.api.gui.form.FormResultHandler
import net.kyori.adventure.text.Component
import org.geysermc.cumulus.form.CustomForm
import org.geysermc.cumulus.response.CustomFormResponse

@Composable
@Suppress("FunctionName")
fun CustomForm(
    title: Component = Component.empty(),
    resultHandler: FormResultHandler<CustomForm, CustomFormResponse> = { _, _ -> },
    contents: ComposableFunction
) {
    ComposeNode<CustomFormNode, Applier<CustomFormNode>>(
        factory = {
            CustomFormNode(
                title = title,
                resultHandler = resultHandler
            )
        },
        update = {
            set(title) { this.title = it }
            set(resultHandler) { this.resultHandler = it }
        },
        content = contents
    )
}