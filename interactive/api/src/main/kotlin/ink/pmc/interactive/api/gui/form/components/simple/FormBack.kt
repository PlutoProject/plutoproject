package ink.pmc.interactive.api.gui.form.components.simple

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import ink.pmc.interactive.api.BACK

@Composable
@Suppress("FunctionName")
fun FormBack() {
    val navigator = LocalNavigator.currentOrThrow
    if (!navigator.canPop) return
    FormButton(
        text = BACK,
        onClick = { _, _ ->
            navigator.pop()
        }
    )
}