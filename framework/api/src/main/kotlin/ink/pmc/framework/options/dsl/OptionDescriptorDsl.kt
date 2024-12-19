package ink.pmc.framework.options.dsl

import ink.pmc.framework.options.EntryValueType
import ink.pmc.framework.options.Limitation
import ink.pmc.framework.options.OptionDescriptor
import ink.pmc.framework.options.factory.OptionDescriptorFactory
import ink.pmc.framework.structure.Builder

class OptionDescriptorDsl<T> : Builder<OptionDescriptor<T>> {
    var key: String? = null
    var type: EntryValueType? = null
    var defaultValue: T? = null
    var objectClass: Class<*>? = null
    var limitation: Limitation<T>? = null

    override fun build(): OptionDescriptor<T> {
        return OptionDescriptorFactory.create(
            requireNotNull(key) { "Key cannot be null" },
            requireNotNull(type) { "Type cannot be null" },
            defaultValue,
            objectClass,
            limitation
        )
    }
}

inline fun <reified T> descriptor(block: OptionDescriptorDsl<T>.() -> Unit): OptionDescriptor<T> {
    return OptionDescriptorDsl<T>()
        .apply { objectClass = T::class.java }
        .apply(block)
        .build()
}