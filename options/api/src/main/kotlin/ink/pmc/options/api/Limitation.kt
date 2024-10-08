package ink.pmc.options.api

interface Limitation<T> {
    val type: EntryValueType

    fun match(value: T): Boolean
}