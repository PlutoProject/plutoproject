package ink.pmc.framework.datastructure

import java.util.*

@Suppress("NOTHING_TO_INLINE")
inline fun <T> mutableConcurrentListOf(vararg elements: T): MutableList<T> {
    return Collections.synchronizedList(mutableListOf(*elements))
}