package ink.pmc.framework.utils.data

import java.util.*

@Suppress("NOTHING_TO_INLINE")
inline fun <T> mutableConcurrentListOf(): MutableList<T> {
    return Collections.synchronizedList(mutableListOf())
}