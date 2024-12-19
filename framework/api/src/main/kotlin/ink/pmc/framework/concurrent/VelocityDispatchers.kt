package ink.pmc.framework.concurrent

import ink.pmc.framework.frameworkVelocity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher

val velocityDispatcher: CoroutineDispatcher
    get() = frameworkVelocity.executorService.asCoroutineDispatcher()