package ink.pmc.framework.concurrent

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.selects.select

@JvmName("composeCollectionJob")
suspend fun compose(tasks: Collection<Job>) {
    return select {
        tasks.forEach {
            it.onJoin
        }
    }
}


@JvmName("composeArrayJob")
suspend fun compose(tasks: Array<Job>) {
    return compose(tasks.toList())
}

@JvmName("composeCollectionDeferred")
suspend fun <T : Any> compose(tasks: Collection<Deferred<T>>): T {
    return select {
        tasks.forEach {
            it.onAwait { result ->
                result
            }
        }
    }
}

@JvmName("composeArrayDeferred")
suspend fun <T : Any> compose(tasks: Array<Deferred<T>>): T {
    return compose(tasks.toList())
}

@JvmName("composeCollectionDeferredCondition")
suspend fun <T> compose(tasks: Collection<Deferred<T?>>, condition: (T?) -> Boolean): T? {
    val remaining = tasks.toMutableList()

    try {
        while (remaining.isNotEmpty()) {
            val selected = select {
                remaining.forEach { deferred ->
                    deferred.onAwait { result ->
                        if (condition(result)) {
                            remaining.forEach { it.cancel() }
                            result
                        } else {
                            remaining.remove(deferred)
                            null
                        }
                    }
                }
            }

            if (selected != null) {
                return selected
            }
        }
    } finally {
        // 确保所有剩余的任务在函数返回之前被取消
        remaining.forEach { it.cancel() }
    }

    return null
}

@JvmName("composeArrayDeferredCondition")
suspend fun <T> compose(tasks: Array<Deferred<T?>>, condition: (T?) -> Boolean): T? {
    return compose(tasks.toMutableList(), condition)
}