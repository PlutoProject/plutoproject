package ink.pmc.framework.options

import ink.pmc.framework.options.EntryValueType
import ink.pmc.framework.options.Limitation
import ink.pmc.framework.options.OptionDescriptor
import ink.pmc.framework.options.factory.OptionDescriptorFactory

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