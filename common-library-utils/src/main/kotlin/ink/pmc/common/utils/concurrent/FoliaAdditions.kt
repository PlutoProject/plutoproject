package ink.pmc.common.utils.concurrent

import ink.pmc.common.utils.platform.isFolia
import kotlinx.coroutines.*

@OptIn(DelicateCoroutinesApi::class)
@Suppress("UNUSED")
fun submitAsyncSafely(block: suspend CoroutineScope.() -> Unit) {
    if (isFolia) {
        runBlocking {
            block()
        }
        return
    }

    GlobalScope.launch {
        block()
    }
}

@Suppress("UNUSED")
suspend fun asyncSafely(block: suspend CoroutineScope.() -> Unit) {
    if (isFolia) {
        runBlocking {
            block()
        }
        return
    }

    async {
        block()
    }
}