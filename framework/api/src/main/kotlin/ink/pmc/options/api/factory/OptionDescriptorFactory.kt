package ink.pmc.options.api.factory

import ink.pmc.framework.utils.inject.inlinedGet
import ink.pmc.options.api.EntryValueType
import ink.pmc.options.api.Limitation
import ink.pmc.options.api.OptionDescriptor

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