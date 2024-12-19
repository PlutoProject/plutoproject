package ink.pmc.framework.structure

@Suppress("NOTHING_TO_INLINE")
inline fun checkMultiple(vararg conditions: Boolean): Boolean {
    return !conditions.any { !it }
}