package ink.pmc.framework.concurrent

import kotlinx.coroutines.*

@Suppress("UNUSED")
@OptIn(DelicateCoroutinesApi::class)
fun submitVelocity(block: suspend CoroutineScope.() -> Unit): Job = GlobalScope.launch(velocityDispatcher) { block() }

fun CoroutineScope.submitVelocity(block: suspend CoroutineScope.() -> Unit): Job =
    launch(velocityDispatcher) { block() }

@Suppress("UNUSED")
suspend fun velocity(block: suspend CoroutineScope.() -> Unit) = withContext(velocityDispatcher) { block() }