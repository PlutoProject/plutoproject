package ink.pmc.options

import ink.pmc.options.api.EntryValueType
import ink.pmc.options.api.Limitation
import ink.pmc.options.api.OptionDescriptor
import ink.pmc.options.api.factory.OptionDescriptorFactory

class OptionDescriptorFactoryImpl : OptionDescriptorFactory {
    override fun <T> create(
        key: String,
        type: EntryValueType,
        defaultValue: T?,
        objectClass: Class<*>?,
        limitation: Limitation<T>?
    ): OptionDescriptor<T> {
        return OptionDescriptorImpl(
            key, type, defaultValue, objectClass, limitation
        )
    }
}