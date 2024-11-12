package ink.pmc.framework.utils.structure

@Suppress("NOTHING_TO_INLINE")
inline fun checkMultiple(vararg conditions: Boolean): Boolean {
    return !conditions.any { !it }
}