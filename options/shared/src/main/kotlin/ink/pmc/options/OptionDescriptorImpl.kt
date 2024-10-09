package ink.pmc.options

import ink.pmc.options.api.EntryValueType
import ink.pmc.options.api.Limitation
import ink.pmc.options.api.OptionDescriptor

data class OptionDescriptorImpl<T>(
    override val key: String,
    override val type: EntryValueType,
    override val defaultValue: T?,
    override val objectClass: Class<*>? = null,
    override val limitation: Limitation<T>? = null
) : OptionDescriptor<T>