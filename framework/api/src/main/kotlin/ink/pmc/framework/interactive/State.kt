package ink.pmc.framework.interactive

import androidx.compose.runtime.MutableState
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@OptIn(DelicateCoroutinesApi::class)
fun MutableState<Int>.stateTransition(
    new: Int,
    resume: Int? = null,
    delay: Duration = 1.seconds,
    coroutineScope: CoroutineScope = GlobalScope,
    navigator: Navigator? = null,
    pop: Boolean = false,
) {
    coroutineScope.launch {
        val keep = value
        value = new
        delay(delay)
        if (!pop || navigator == null) value = resume ?: keep
        if (pop && navigator != null) navigator.pop()
    }
}