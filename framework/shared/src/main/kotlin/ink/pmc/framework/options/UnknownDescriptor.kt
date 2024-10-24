package ink.pmc.framework.options

import ink.pmc.framework.options.EntryValueType
import ink.pmc.framework.options.Limitation
import ink.pmc.framework.options.OptionDescriptor

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