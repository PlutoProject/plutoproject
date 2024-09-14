package ink.pmc.framework.options

data class OptionDescriptorImpl<T>(
    override val key: String,
    override val type: EntryValueType,
    override val defaultValue: T?,
    override val objectClass: Class<*>? = null,
    override val limitation: Limitation<T>? = null
) : OptionDescriptor<T>