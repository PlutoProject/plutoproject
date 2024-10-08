package ink.pmc.options

import ink.pmc.options.api.EntryValueType
import ink.pmc.options.api.OptionDescriptor
import ink.pmc.options.api.OptionEntry
import ink.pmc.options.api.OptionsContainer
import ink.pmc.options.models.toModel
import ink.pmc.options.repositories.OptionsContainerRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

class OptionsContainerImpl(
    override val owner: UUID,
    private val entriesMap: MutableMap<String, OptionEntry<*>>
) : OptionsContainer, KoinComponent {
    private val repo by inject<OptionsContainerRepository>()
    override val entries: List<OptionEntry<*>>
        get() = entriesMap.values.toList()

    override fun <T : Any> contains(descriptor: OptionDescriptor<T>): Boolean {
        return entriesMap.containsKey(descriptor.key)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> getEntry(descriptor: OptionDescriptor<T>): OptionEntry<T>? {
        return if (contains(descriptor)) {
            entriesMap[descriptor.key] as? OptionEntry<T>
        } else {
            null
        }
    }

    override fun <T : Any> setEntry(descriptor: OptionDescriptor<T>, value: T) {
        if (descriptor.type == EntryValueType.OBJECT) {
            val objClass = checkNotNull(descriptor.objectClass) { "Object class cannot be null: ${descriptor.key}" }
            require(objClass.isInstance(value)) { "Value must be a instance of ${objClass.name}" }
        }
        entriesMap[descriptor.key] = OptionEntryImpl(descriptor, value)
    }

    override fun removeEntry(descriptor: OptionDescriptor<*>) {
        entriesMap.remove(descriptor.key)
    }

    override suspend fun save() {
        repo.saveOrUpdate(this.toModel())
    }
}