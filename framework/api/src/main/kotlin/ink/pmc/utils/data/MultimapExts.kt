package ink.pmc.utils.data

import com.google.common.collect.Multimap

@Suppress("NOTHING_TO_INLINE")
inline operator fun <K, V> Multimap<K, V>.set(key: K, value: V) {
    put(key, value)
}

@Suppress("NOTHING_TO_INLINE")
inline operator fun <K, V> Multimap<K, V>.set(key: K, value: Iterable<V>) {
    putAll(key, value)
}