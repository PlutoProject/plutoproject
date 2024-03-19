package ink.pmc.common.utils.concurrent

import kotlinx.coroutines.*
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.entity.Entity
import kotlin.coroutines.CoroutineContext

@Suppress("UNUSED")
@OptIn(DelicateCoroutinesApi::class)
fun async(block: suspend CoroutineScope.() -> Unit): Job = GlobalScope.launch { block() }

@Suppress("UNUSED")
@OptIn(DelicateCoroutinesApi::class)
fun async(coroutineContext: CoroutineContext, block: suspend CoroutineScope.() -> Unit): Job = GlobalScope.launch(coroutineContext) { block() }

@Suppress("UNUSED")
@OptIn(DelicateCoroutinesApi::class)
fun sync(block: suspend CoroutineScope.() -> Unit): Job = GlobalScope.launch(mainThreadDispatcher) { block() }

@Suppress("UNUSED")
@OptIn(DelicateCoroutinesApi::class)
fun sync(entity: Entity, block: suspend CoroutineScope.() -> Unit) = GlobalScope.launch(entity.dispatcher) { block() }

@Suppress("UNUSED")
@OptIn(DelicateCoroutinesApi::class)
fun sync(chunk: Chunk, block: suspend CoroutineScope.() -> Unit) = GlobalScope.launch(chunk.dispatcher) { block() }

@Suppress("UNUSED")
@OptIn(DelicateCoroutinesApi::class)
fun sync(location: Location, block: suspend CoroutineScope.() -> Unit) = GlobalScope.launch(location.dispatcher) { block() }

@Suppress("UNUSED")
@OptIn(DelicateCoroutinesApi::class)
fun Entity.sync(block: suspend CoroutineScope.() -> Unit) = GlobalScope.launch(this.dispatcher) { block() }

@Suppress("UNUSED")
@OptIn(DelicateCoroutinesApi::class)
fun Chunk.sync(block: suspend CoroutineScope.() -> Unit) = GlobalScope.launch(this.dispatcher) { block() }

@Suppress("UNUSED")
@OptIn(DelicateCoroutinesApi::class)
fun Location.sync(block: suspend CoroutineScope.() -> Unit) = GlobalScope.launch(this.dispatcher) { block() }