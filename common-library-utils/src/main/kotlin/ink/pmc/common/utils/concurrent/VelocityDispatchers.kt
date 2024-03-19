package ink.pmc.common.utils.concurrent

import ink.pmc.common.utils.platform.velocityUtilsPlugin
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher

val velocityDispatcher: CoroutineDispatcher
    get() = velocityUtilsPlugin.executorService.asCoroutineDispatcher()