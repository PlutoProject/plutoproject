package ink.pmc.framework.options.factory

import ink.pmc.framework.options.EntryValueType
import ink.pmc.framework.options.Limitation
import ink.pmc.framework.options.OptionDescriptor
import ink.pmc.framework.inject.inlinedGet

interface OptionDescriptorFactory {
    companion object : OptionDescriptorFactory by inlinedGet()

    fun <T> create(
        key: String,
        type: EntryValueType,
        defaultValue: T? = null,
        objectClass: Class<*>? = null,
        limitation: Limitation<T>? = null
    ): OptionDescriptor<T>
}