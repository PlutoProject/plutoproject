package ink.pmc.framework.concurrent

import kotlinx.coroutines.*
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.entity.Entity

@Suppress("UNUSED")
@OptIn(DelicateCoroutinesApi::class)
inline fun submitSync(crossinline block: suspend CoroutineScope.() -> Unit) =
    GlobalScope.launch(paperDispatcher) { block() }

@Suppress("UNUSED")
@OptIn(DelicateCoroutinesApi::class)
inline fun <T> submitSync(crossinline block: suspend CoroutineScope.() -> T) =
    GlobalScope.async(paperDispatcher) { block() }

@Suppress("UNUSED")
@OptIn(DelicateCoroutinesApi::class)
@JvmName("entitySync")
inline fun Entity.submitSync(
    scope: CoroutineScope = GlobalScope,
    crossinline block: suspend CoroutineScope.() -> Unit
) =
    scope.launch(this.dispatcher) { block() }

@Suppress("UNUSED")
@OptIn(DelicateCoroutinesApi::class)
@JvmName("chunkSync")
inline fun Chunk.submitSync(scope: CoroutineScope = GlobalScope, crossinline block: suspend CoroutineScope.() -> Unit) =
    scope.launch(this.dispatcher) { block() }

@Suppress("UNUSED")
@OptIn(DelicateCoroutinesApi::class)
@JvmName("locationSync")
inline fun Location.submitSync(
    scope: CoroutineScope = GlobalScope,
    crossinline block: suspend CoroutineScope.() -> Unit
) =
    scope.launch(this.dispatcher) { block() }

@Suppress("UNUSED")
@OptIn(DelicateCoroutinesApi::class)
@JvmName("entitySync")
inline fun <T> Entity.submitSync(
    scope: CoroutineScope = GlobalScope,
    crossinline block: suspend CoroutineScope.() -> T
) =
    scope.async(this.dispatcher) { block() }

@Suppress("UNUSED")
@OptIn(DelicateCoroutinesApi::class)
@JvmName("chunkSync")
inline fun <T> Chunk.submitSync(
    scope: CoroutineScope = GlobalScope,
    crossinline block: suspend CoroutineScope.() -> T
) =
    scope.async(this.dispatcher) { block() }

@Suppress("UNUSED")
@OptIn(DelicateCoroutinesApi::class)
@JvmName("locationSync")
inline fun <T> Location.submitSync(
    scope: CoroutineScope = GlobalScope,
    crossinline block: suspend CoroutineScope.() -> T
) =
    scope.async(this.dispatcher) { block() }

@Suppress("UNUSED")
suspend inline fun sync(crossinline block: suspend CoroutineScope.() -> Unit) = withContext(paperDispatcher) { block() }

@Suppress("UNUSED")
suspend inline fun Entity.sync(crossinline block: suspend CoroutineScope.() -> Unit) =
    withContext(this.dispatcher) { block() }

@Suppress("UNUSED")
suspend inline fun Chunk.sync(crossinline block: suspend CoroutineScope.() -> Unit) =
    withContext(this.dispatcher) { block() }

@Suppress("UNUSED")
suspend inline fun Location.sync(crossinline block: suspend CoroutineScope.() -> Unit) =
    withContext(this.dispatcher) { block() }

@Suppress("UNUSED")
@JvmName("syncValue")
suspend inline fun <T> sync(crossinline block: suspend CoroutineScope.() -> T) =
    withContext(paperDispatcher) { block() }

@Suppress("UNUSED")
@JvmName("entitySyncValue")
suspend inline fun <T> Entity.sync(crossinline block: suspend CoroutineScope.() -> T) =
    withContext(this.dispatcher) { block() }

@Suppress("UNUSED")
@JvmName("chunkSyncValue")
suspend inline fun <T> Chunk.sync(crossinline block: suspend CoroutineScope.() -> T) =
    withContext(this.dispatcher) { block() }

@Suppress("UNUSED")
@JvmName("locationSyncValue")
suspend inline fun <T> Location.sync(crossinline block: suspend CoroutineScope.() -> T) =
    withContext(this.dispatcher) { block() }