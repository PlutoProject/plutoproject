package ink.pmc.options.api

interface OptionDescriptor<T> {
    val key: String
    val type: EntryValueType
    val defaultValue: T?
    val objectClass: Class<*>?
    val limitation: Limitation<T>?
}