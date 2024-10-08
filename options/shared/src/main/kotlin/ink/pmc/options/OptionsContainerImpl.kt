package ink.pmc.options

import ink.pmc.options.api.EntryValueType
import ink.pmc.options.api.OptionDescriptor
import ink.pmc.options.api.OptionEntry
import ink.pmc.options.api.OptionsContainer

class OptionsContainerImpl : OptionsContainer {
    private val entries = mutableMapOf<String, OptionEntry<*>>()

    override fun <T> contains(descriptor: OptionDescriptor<T>): Boolean {
        return entries.containsKey(descriptor.key)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> getEntry(descriptor: OptionDescriptor<T>): OptionEntry<T>? {
        return if (contains(descriptor)) {
            entries[descriptor.key] as? OptionEntry<T>
        } else {
            null
        }
    }

    override fun <T> setEntry(descriptor: OptionDescriptor<T>, value: T) {
        if (descriptor.type == EntryValueType.OBJECT) {
            val objClass = checkNotNull(descriptor.objectClass) { "Object class cannot be null: ${descriptor.key}" }
            require(objClass.isInstance(value)) { "Value must be a instance of ${objClass.name}" }
        }
        entries[descriptor.key] = OptionEntryImpl(descriptor, value)
    }

    override fun removeEntry(descriptor: OptionDescriptor<*>) {
        entries.remove(descriptor.key)
    }
}