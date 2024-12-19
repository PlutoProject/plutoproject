package ink.pmc.framework.datastructure

import java.util.concurrent.ConcurrentHashMap

@Suppress("NOTHING_TO_INLINE")
inline fun <T> mutableConcurrentSetOf(vararg elements: T): MutableSet<T> {
    return ConcurrentHashMap.newKeySet()
}