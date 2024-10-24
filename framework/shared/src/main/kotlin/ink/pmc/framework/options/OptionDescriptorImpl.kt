package ink.pmc.framework.options

import ink.pmc.framework.options.EntryValueType
import ink.pmc.framework.options.Limitation
import ink.pmc.framework.options.OptionDescriptor

data class OptionDescriptorImpl<T>(
    override val key: String,
    override val type: EntryValueType,
    override val defaultValue: T?,
    override val objectClass: Class<*>? = null,
    override val limitation: Limitation<T>? = null
) : OptionDescriptor<T>