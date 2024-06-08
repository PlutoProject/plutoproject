package ink.pmc.utils.concurrent

import ink.pmc.utils.platform.velocityUtilsPlugin
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher

val velocityDispatcher: CoroutineDispatcher
    get() = velocityUtilsPlugin.executorService.asCoroutineDispatcher()