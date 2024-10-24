package ink.pmc.framework.options

import ink.pmc.options.api.EntryValueType
import ink.pmc.options.api.Limitation
import ink.pmc.options.api.OptionDescriptor

/*
* 仅运行时存在，内部使用。
* */
class UnknownDescriptor(val originalType: EntryValueType) : OptionDescriptor<String> {
    override val key: String = ""
    override val type: EntryValueType = EntryValueType.UNKNOWN
    override val defaultValue: String? = null
    override val objectClass: Class<*>? = null
    override val limitation: Limitation<String>? = null
}