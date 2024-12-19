package ink.pmc.framework.concurrent

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

// submitAsync start

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
fun <T> submitAsync(coroutineContext: CoroutineContext, block: suspend CoroutineScope.() -> T): Deferred<T> =
    GlobalScope.async(coroutineContext) { block() }

// submitAsync end

// async start

@Suppress("UNUSED")
suspend fun async(block: suspend CoroutineScope.() -> Unit) = withContext(Dispatchers.Default) { block() }

@Suppress("UNUSED")
suspend fun async(coroutineContext: CoroutineContext, block: suspend CoroutineScope.() -> Unit) =
    withContext(coroutineContext) { block() }

@Suppress("UNUSED")
@JvmName("asyncValue")
suspend fun <T> async(block: suspend CoroutineScope.() -> T) = withContext(Dispatchers.Default) { block() }

@Suppress("UNUSED")
@JvmName("asyncValue")
suspend fun <T> async(coroutineContext: CoroutineContext, block: suspend CoroutineScope.() -> T) =
    withContext(coroutineContext) { block() }

// async end

// submitAsyncIO start

@Suppress("UNUSED")
@OptIn(DelicateCoroutinesApi::class)
fun submitAsyncIO(block: suspend CoroutineScope.() -> Unit) = GlobalScope.launch(Dispatchers.IO) { block() }

@Suppress("UNUSED")
@OptIn(DelicateCoroutinesApi::class)
fun <T> submitAsyncIO(block: suspend CoroutineScope.() -> T): Deferred<T> =
    GlobalScope.async(Dispatchers.IO) { block() }

// submitAsyncIO end

// submitAsyncUnconfined start

@Suppress("UNUSED")
@OptIn(DelicateCoroutinesApi::class)
fun submitAsyncUnconfined(block: suspend CoroutineScope.() -> Unit) =
    GlobalScope.launch(Dispatchers.Unconfined) { block() }

@Suppress("UNUSED")
@OptIn(DelicateCoroutinesApi::class)
fun <T> submitAsyncUnconfined(block: suspend CoroutineScope.() -> T) =
    GlobalScope.async(Dispatchers.Unconfined) { block() }

// submitAsyncUnconfined end

@Suppress("UNUSED")
suspend fun io(block: suspend CoroutineScope.() -> Unit) = withContext(Dispatchers.IO) { block() }

@Suppress("UNUSED")
suspend fun unconfined(block: suspend CoroutineScope.() -> Unit) = withContext(Dispatchers.Unconfined) { block() }