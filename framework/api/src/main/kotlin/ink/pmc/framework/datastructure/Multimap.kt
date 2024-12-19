package ink.pmc.framework.datastructure

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.Multimap
import com.google.common.collect.Multimaps

@Suppress("NOTHING_TO_INLINE")
inline fun <K, V> listMultimapOf(): Multimap<K, V> {
    return ArrayListMultimap.create()
}

@Suppress("NOTHING_TO_INLINE")
inline fun <K, V> concurrentListMultimapOf(): Multimap<K, V> {
    return Multimaps.synchronizedMultimap(listMultimapOf())
}