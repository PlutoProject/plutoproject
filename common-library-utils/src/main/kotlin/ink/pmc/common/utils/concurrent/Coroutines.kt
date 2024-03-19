package ink.pmc.common.utils.concurrent

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

@Suppress("UNUSED")
@OptIn(DelicateCoroutinesApi::class)
fun submitAsync(block: suspend CoroutineScope.() -> Unit): Job = GlobalScope.launch { block() }

@Suppress("UNUSED")
@OptIn(DelicateCoroutinesApi::class)
fun submitAsync(coroutineContext: CoroutineContext, block: suspend CoroutineScope.() -> Unit): Job =
    GlobalScope.launch(coroutineContext) { block() }

@Suppress("UNUSED")
suspend fun async(block: suspend CoroutineScope.() -> Unit) = withContext(Dispatchers.Default) { block() }

@Suppress("UNUSED")
suspend fun async(coroutineContext: CoroutineContext, block: suspend CoroutineScope.() -> Unit) =
    withContext(coroutineContext) { block() }