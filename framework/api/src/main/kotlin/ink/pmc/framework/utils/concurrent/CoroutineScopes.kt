package ink.pmc.framework.utils.concurrent

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel

val frameworkDefaultScope = CoroutineScope(Dispatchers.Default)
val frameworkIoScope = CoroutineScope(Dispatchers.IO)

fun cancelFrameworkScopes() = runCatching {
    frameworkDefaultScope.cancel()
    frameworkIoScope.cancel()
}