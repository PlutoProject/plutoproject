package ink.pmc.options.api

interface Limitation<T> {
    fun match(value: T): Boolean
}