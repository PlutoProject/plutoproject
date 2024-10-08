package ink.pmc.options.api

interface OptionsContainer {
    fun contains(descriptor: OptionsDescriptor<*>)

    fun getEntry(descriptor: OptionsDescriptor<*>): OptionEntry<*>?

    fun <T> setEntry(descriptor: OptionsDescriptor<T>, value: T)

    fun removeEntry(descriptor: OptionsDescriptor<*>)
}