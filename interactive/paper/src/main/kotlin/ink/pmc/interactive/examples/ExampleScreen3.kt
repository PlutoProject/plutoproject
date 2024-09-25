package ink.pmc.interactive.examples

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import ink.pmc.advkt.component.component
import ink.pmc.advkt.component.text
import ink.pmc.interactive.api.form.components.simple.FormButton
import ink.pmc.interactive.api.form.types.SimpleForm
import ink.pmc.interactive.examples.form.ExampleFormScreen1
import ink.pmc.utils.visual.mochaGreen
import ink.pmc.utils.visual.mochaYellow

@Suppress("UNUSED")
class ExampleScreen3 : Screen {

    override val key: ScreenKey = "interactive_example_3"

    @Composable
    override fun Content() {
        SimpleForm(
            title = component { text("你好") with mochaGreen },
            content = component { text("欢迎使用菜单") })
        {
            InnerContents()
        }
    }

    @Composable
    @Suppress("FunctionName")
    private fun InnerContents() {
        val navigator = LocalNavigator.current
        repeat(30) {
            FormButton(
                text = component { text("按钮 $it") with mochaYellow },
                onClick = { _, _ ->
                    navigator?.push(ExampleFormScreen1(it))
                }
            )
        }
    }

}