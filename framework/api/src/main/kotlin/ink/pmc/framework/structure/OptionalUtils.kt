package ink.pmc.framework.structure

import java.util.*

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <T> optional(obj: T?): Optional<T> {
    return Optional.ofNullable(obj) as Optional<T>
}

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <T> emptyOptional(): Optional<T> {
    return Optional.empty<T>() as Optional<T>
}