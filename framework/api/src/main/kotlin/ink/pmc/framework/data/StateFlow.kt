package ink.pmc.framework.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.reflect.KProperty

@Suppress("NOTHING_TO_INLINE")
inline operator fun <T, V> StateFlow<V>.getValue(t: T, property: KProperty<*>): V {
    return value
}

@Suppress("NOTHING_TO_INLINE")
inline operator fun <T, V> MutableStateFlow<V>.setValue(t: T, property: KProperty<*>, value: V) {
    this.value = value
}