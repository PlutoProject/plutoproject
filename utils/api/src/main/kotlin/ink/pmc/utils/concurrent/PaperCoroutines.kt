package ink.pmc.utils.concurrent

import ink.pmc.utils.platform.paperUtilsPlugin
import kotlinx.coroutines.*
import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.plugin.java.JavaPlugin

// submitSync start

@Suppress("UNUSED")
@OptIn(DelicateCoroutinesApi::class)
fun submitSync(block: suspend CoroutineScope.() -> Unit) = GlobalScope.launch(mainThreadDispatcher) { block() }

@Suppress("UNUSED")
@OptIn(DelicateCoroutinesApi::class)
fun <T> submitSync(block: suspend CoroutineScope.() -> T) = GlobalScope.async(mainThreadDispatcher) { block() }

@Suppress("UNUSED")
@OptIn(DelicateCoroutinesApi::class)
@JvmName("submitSyncEntity")
fun submitSync(entity: Entity, block: suspend CoroutineScope.() -> Unit) =
    GlobalScope.launch(entity.dispatcher) { block() }

@Suppress("UNUSED")
@OptIn(DelicateCoroutinesApi::class)
@JvmName("submitSyncChunk")
fun submitSync(chunk: Chunk, block: suspend CoroutineScope.() -> Unit) =
    GlobalScope.launch(chunk.dispatcher) { block() }

@Suppress("UNUSED")
@OptIn(DelicateCoroutinesApi::class)
@JvmName("submitSyncLocation")
fun submitSync(location: Location, block: suspend CoroutineScope.() -> Unit) =
    GlobalScope.launch(location.dispatcher) { block() }

@Suppress("UNUSED")
@OptIn(DelicateCoroutinesApi::class)
@JvmName("submitSyncEntity")
fun <T> submitSync(entity: Entity, block: suspend CoroutineScope.() -> T) =
    GlobalScope.async(entity.dispatcher) { block() }

@Suppress("UNUSED")
@OptIn(DelicateCoroutinesApi::class)
@JvmName("submitSyncChunk")
fun <T> submitSync(chunk: Chunk, block: suspend CoroutineScope.() -> T) =
    GlobalScope.async(chunk.dispatcher) { block() }

@Suppress("UNUSED")
@OptIn(DelicateCoroutinesApi::class)
@JvmName("submitSyncLocation")
fun <T> submitSync(location: Location, block: suspend CoroutineScope.() -> T) =
    GlobalScope.async(location.dispatcher) { block() }

// submitSync end

@Suppress("UNUSED")
suspend fun sync(block: suspend CoroutineScope.() -> Unit) = withContext(mainThreadDispatcher) { block() }

@Suppress("UNUSED")
@JvmName("syncEntitySuspend")
suspend fun sync(entity: Entity, block: suspend CoroutineScope.() -> Unit) =
    withContext(entity.dispatcher) { block() }

@Suppress("UNUSED")
@JvmName("syncChunkSuspend")
suspend fun sync(chunk: Chunk, block: suspend CoroutineScope.() -> Unit) =
    withContext(chunk.dispatcher) { block() }

@Suppress("UNUSED")
@JvmName("syncLocationSuspend")
suspend fun sync(location: Location, block: suspend CoroutineScope.() -> Unit) =
    withContext(location.dispatcher) { block() }

// value submitSync start

@Suppress("UNUSED")
@OptIn(DelicateCoroutinesApi::class)
@JvmName("entitySync")
fun Entity.submitSync(scope: CoroutineScope = GlobalScope, block: suspend CoroutineScope.() -> Unit) = scope.launch(this.dispatcher) { block() }

@Suppress("UNUSED")
@OptIn(DelicateCoroutinesApi::class)
@JvmName("chunkSync")
fun Chunk.submitSync(scope: CoroutineScope = GlobalScope, block: suspend CoroutineScope.() -> Unit) = scope.launch(this.dispatcher) { block() }

@Suppress("UNUSED")
@OptIn(DelicateCoroutinesApi::class)
@JvmName("locationSync")
fun Location.submitSync(scope: CoroutineScope = GlobalScope, block: suspend CoroutineScope.() -> Unit) = scope.launch(this.dispatcher) { block() }

@Suppress("UNUSED")
@OptIn(DelicateCoroutinesApi::class)
@JvmName("entitySync")
fun <T> Entity.submitSync(scope: CoroutineScope = GlobalScope, block: suspend CoroutineScope.() -> T) = scope.async(this.dispatcher) { block() }

@Suppress("UNUSED")
@OptIn(DelicateCoroutinesApi::class)
@JvmName("chunkSync")
fun <T> Chunk.submitSync(scope: CoroutineScope = GlobalScope, block: suspend CoroutineScope.() -> T) = scope.async(this.dispatcher) { block() }

@Suppress("UNUSED")
@OptIn(DelicateCoroutinesApi::class)
@JvmName("locationSync")
fun <T> Location.submitSync(scope: CoroutineScope = GlobalScope, block: suspend CoroutineScope.() -> T) = scope.async(this.dispatcher) { block() }

// value submitSync end

@Suppress("UNUSED")
@JvmName("entitySyncSuspend")
suspend fun Entity.sync(block: suspend CoroutineScope.() -> Unit) = withContext(this.dispatcher) { block() }

@Suppress("UNUSED")
@JvmName("chunkSyncSuspend")
suspend fun Chunk.sync(block: suspend CoroutineScope.() -> Unit) = withContext(this.dispatcher) { block() }

@Suppress("UNUSED")
@JvmName("locationSyncSuspend")
suspend fun Location.sync(block: suspend CoroutineScope.() -> Unit) = withContext(this.dispatcher) { block() }

@Suppress("UNUSED")
@JvmName("entitySyncSuspendValue")
suspend fun <T> Entity.sync(block: suspend CoroutineScope.() -> T) = withContext(this.dispatcher) { block() }

@Suppress("UNUSED")
@JvmName("chunkSyncSuspendValue")
suspend fun <T> Chunk.sync(block: suspend CoroutineScope.() -> T) = withContext(this.dispatcher) { block() }

@Suppress("UNUSED")
@JvmName("locationSyncSuspendValue")
suspend fun <T> Location.sync(block: suspend CoroutineScope.() -> T) = withContext(this.dispatcher) { block() }

// Schedulers

@Suppress("UNUSED")
fun Entity.scheduler(plugin: JavaPlugin, retired: () -> Unit = {}, delay: Long = 0L, task: () -> Unit) {
    this.scheduler.execute(plugin, task, retired, delay)
}

@Suppress("UNUSED")
fun globalRegionScheduler(plugin: JavaPlugin, task: () -> Unit) {
    Bukkit.getServer().globalRegionScheduler.execute(plugin, task)
}

@Suppress("UNUSED")
fun regionScheduler(plugin: JavaPlugin, location: Location, task: () -> Unit) {
    Bukkit.getServer().regionScheduler.execute(plugin, location, task)
}

@Suppress("UNUSED")
fun regionScheduler(plugin: JavaPlugin, chunk: Chunk, task: () -> Unit) {
    Bukkit.getServer().regionScheduler.execute(plugin, chunk.world, chunk.x, chunk.z, task)
}

@Suppress("UNUSED")
fun Entity.scheduler(retired: () -> Unit = {}, delay: Long = 0L, task: () -> Unit) {
    this.scheduler.execute(paperUtilsPlugin, task, retired, delay)
}

@Suppress("UNUSED")
fun globalRegionScheduler(task: () -> Unit) {
    Bukkit.getServer().globalRegionScheduler.execute(paperUtilsPlugin, task)
}

@Suppress("UNUSED")
fun regionScheduler(location: Location, task: () -> Unit) {
    Bukkit.getServer().regionScheduler.execute(paperUtilsPlugin, location, task)
}

@Suppress("UNUSED")
fun regionScheduler(chunk: Chunk, task: () -> Unit) {
    Bukkit.getServer().regionScheduler.execute(paperUtilsPlugin, chunk.world, chunk.x, chunk.z, task)
}

fun Chunk.scheduler(task: () -> Unit) {
    regionScheduler(this, task)
}

fun Location.scheduler(task: () -> Unit) {
    regionScheduler(this, task)
}