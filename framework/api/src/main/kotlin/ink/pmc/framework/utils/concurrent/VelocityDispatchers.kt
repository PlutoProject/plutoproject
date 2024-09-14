package ink.pmc.framework.utils.concurrent

import ink.pmc.framework.frameworkVelocity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher

val velocityDispatcher: CoroutineDispatcher
    get() = frameworkVelocity.executorService.asCoroutineDispatcher()