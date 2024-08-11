package ink.pmc.interactive.api.form.components.custom

import androidx.compose.runtime.Applier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import net.kyori.adventure.text.Component

@Composable
@Suppress("FunctionName")
fun FormStepSlider(
    text: Component = Component.empty(),
    steps: Collection<String> = listOf(),
    defaultStep: Int = 0
) {
    ComposeNode<FormStepSliderNode, Applier<Any>>(
        factory = {
            FormStepSliderNode(
                text = text,
                steps = steps,
                defaultStep = defaultStep
            )
        },
        update = {
            set(text) { this.text = it }
            set(steps) { this.steps = it }
            set(defaultStep) { this.defaultStep = it }
        }
    )
}