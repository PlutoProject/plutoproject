package ink.pmc.essentials.screens.examples

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import ink.pmc.advkt.component.component
import ink.pmc.advkt.component.text
import ink.pmc.advkt.component.yellow
import ink.pmc.interactive.api.form.components.simple.FormButton
import ink.pmc.interactive.api.form.types.SimpleForm
import ink.pmc.utils.visual.mochaGreen
import ink.pmc.utils.visual.mochaYellow

@Suppress("UNUSED")
class ExampleScreen3 : Screen {

    override val key: ScreenKey = "essentials_example_3"

    @Composable
    override fun Content() {
        SimpleForm(
            title = component { text("你好") with mochaGreen },
            content = component { text("颜色测试") with yellow() },
            resultHandler = { _, _ ->
                println("Result received")
            }
        ) {
            InnerContents()
        }
    }

    @Composable
    @Suppress("FunctionName")
    private fun InnerContents() {
        FormButton(
            text = component { text("测试按钮") with mochaYellow  }
        )
    }

}