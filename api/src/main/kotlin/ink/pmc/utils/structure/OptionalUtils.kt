package ink.pmc.utils.structure

import java.util.Optional

@Suppress("UNCHECKED_CAST")
fun <T> optional(obj: T?): Optional<T> {
    return Optional.ofNullable(obj) as Optional<T>
}

@Suppress("UNCHECKED_CAST")
fun <T> emptyOptional(): Optional<T> {
    return Optional.empty<T>() as Optional<T>
}