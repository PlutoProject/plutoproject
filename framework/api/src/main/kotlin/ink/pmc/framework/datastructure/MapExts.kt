package ink.pmc.framework.datastructure

inline fun <K, V, R1, R2> Map<K, V>.mapKv(transformer: (Map.Entry<K, V>) -> Pair<R1, R2>): Map<R1, R2> {
    return entries.associate { transformer(it) }
}