package ink.pmc.options.api

interface OptionsDescriptor<T> {
    val id: String
    val type: EntryValueType
    val defaultValue: T?
    val objectClass: Class<*>?
}