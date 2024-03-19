package ink.pmc.common.utils.concurrent

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

@Suppress("UNUSED")
@OptIn(DelicateCoroutinesApi::class)
fun async(block: suspend CoroutineScope.() -> Unit): Job = GlobalScope.launch { block() }

@Suppress("UNUSED")
@OptIn(DelicateCoroutinesApi::class)
fun async(coroutineContext: CoroutineContext, block: suspend CoroutineScope.() -> Unit): Job =
    GlobalScope.launch(coroutineContext) { block() }

@Suppress("UNUSED")
suspend fun asyncContext(block: suspend CoroutineScope.() -> Unit) = withContext(Dispatchers.Default) { block() }

@Suppress("UNUSED")
suspend fun asyncContext(coroutineContext: CoroutineContext, block: suspend CoroutineScope.() -> Unit) =
    withContext(coroutineContext) { block() }