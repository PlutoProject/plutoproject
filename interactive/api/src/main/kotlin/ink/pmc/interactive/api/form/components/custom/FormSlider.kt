package ink.pmc.interactive.api.form.components.custom

import androidx.compose.runtime.Applier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import net.kyori.adventure.text.Component

@Composable
@Suppress("FunctionName")
fun FormSlider(
    text: Component = Component.empty(),
    min: Float,
    max: Float,
    step: Float,
    defaultValue: Float
) {
    ComposeNode<FormSliderNode, Applier<Any>>(
        factory = {
            FormSliderNode(
                text = text,
                min = min,
                max = max,
                step = step,
                defaultValue = defaultValue
            )
        },
        update = {
            set(text) { this.text = it }
            set(min) { this.min = it }
            set(max) { this.max = it }
            set(step) { this.step = it }
            set(defaultValue) { this.defaultValue = it }
        }
    )
}