package ink.pmc.options.api

interface OptionsContainer {
    fun <T> contains(descriptor: OptionDescriptor<T>): Boolean

    fun <T> getEntry(descriptor: OptionDescriptor<T>): OptionEntry<T>?

    fun <T> setEntry(descriptor: OptionDescriptor<T>, value: T)

    fun removeEntry(descriptor: OptionDescriptor<*>)
}