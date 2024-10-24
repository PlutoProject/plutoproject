package ink.pmc.framework.options

interface Limitation<T> {
    fun match(value: T): Boolean
}