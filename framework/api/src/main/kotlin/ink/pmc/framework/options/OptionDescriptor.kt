package ink.pmc.framework.options

interface OptionDescriptor<T> {
    val key: String
    val type: EntryValueType
    val defaultValue: T?
    val objectClass: Class<*>?
    val limitation: Limitation<T>?
}