package ink.pmc.essentials.screens.examples

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import ink.pmc.advkt.component.component
import ink.pmc.advkt.component.text
import ink.pmc.interactive.api.LocalGuiScope
import ink.pmc.interactive.api.form.types.ModalForm
import ink.pmc.utils.visual.mochaText

class ExampleScreen4(private val id: Int) : Screen {

    override val key: ScreenKey = "essentials_example_4"

    @Composable
    override fun Content() {
        val scope = LocalGuiScope.current
        val navigator = LocalNavigator.current
        ModalForm(
            title = component { text("点击反馈") with mochaText },
            content = component { text("你点击了按钮 $id") with mochaText },
            button1 = component { text("点我返回上一页") },
            button2 = component { text("点我关闭菜单") },
            onButton1 = { _, _ ->
                navigator?.pop()
            },
            onButton2 = { _, _ ->
                scope.dispose()
            }
        )
    }

}