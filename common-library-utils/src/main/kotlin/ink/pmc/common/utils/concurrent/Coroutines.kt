package ink.pmc.common.utils.concurrent

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

@Suppress("UNUSED")
@OptIn(DelicateCoroutinesApi::class)
fun submitAsync(block: suspend CoroutineScope.() -> Unit) = GlobalScope.launch { block() }

@Suppress("UNUSED")
@OptIn(DelicateCoroutinesApi::class)
fun submitAsync(coroutineContext: CoroutineContext, block: suspend CoroutineScope.() -> Unit) =
    GlobalScope.launch(coroutineContext) { block() }

@Suppress("UNUSED")
@OptIn(DelicateCoroutinesApi::class)
fun <T> submitAsync(block: suspend CoroutineScope.() -> T): Deferred<T> = GlobalScope.async { block() }

@Suppress("UNUSED")
@OptIn(DelicateCoroutinesApi::class)
fun <T> submitAsync(coroutineContext: CoroutineContext, block: suspend CoroutineScope.() -> T) =
    GlobalScope.async(coroutineContext) { block() }

@Suppress("UNUSED")
suspend fun async(block: suspend CoroutineScope.() -> Unit) = withContext(Dispatchers.Default) { block() }

@Suppress("UNUSED")
suspend fun async(coroutineContext: CoroutineContext, block: suspend CoroutineScope.() -> Unit) =
    withContext(coroutineContext) { block() }

@Suppress("UNUSED")
@OptIn(DelicateCoroutinesApi::class)
fun submitAsyncIO(block: suspend CoroutineScope.() -> Unit) = GlobalScope.launch(Dispatchers.IO) { block() }

@Suppress("UNUSED")
@OptIn(DelicateCoroutinesApi::class)
fun <T> submitAsyncIO(block: suspend CoroutineScope.() -> T) = GlobalScope.async(Dispatchers.IO) { block() }

@Suppress("UNUSED")
@OptIn(DelicateCoroutinesApi::class)
fun submitAsyncUnconfined(block: suspend CoroutineScope.() -> Unit) =
    GlobalScope.launch(Dispatchers.Unconfined) { block() }

@Suppress("UNUSED")
@OptIn(DelicateCoroutinesApi::class)
fun <T> submitAsyncUnconfined(block: suspend CoroutineScope.() -> T) =
    GlobalScope.async(Dispatchers.Unconfined) { block() }

@Suppress("UNUSED")
suspend fun io(block: suspend CoroutineScope.() -> Unit) = withContext(Dispatchers.IO) { block() }

@Suppress("UNUSED")
suspend fun unconfined(block: suspend CoroutineScope.() -> Unit) = withContext(Dispatchers.Unconfined) { block() }