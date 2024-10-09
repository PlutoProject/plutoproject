package ink.pmc.options

import ink.pmc.options.api.*
import ink.pmc.options.models.toModel
import ink.pmc.options.repositories.OptionsContainerRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

class PlayerOptionsImpl(
    override val player: UUID,
    private val entriesMap: MutableMap<String, OptionEntry<*>>
) : PlayerOptions, KoinComponent {
    private val repo by inject<OptionsContainerRepository>()
    private val notifier by inject<OptionsUpdateNotifier>()
    override val entries: List<OptionEntry<*>>
        get() = entriesMap.values.toList()

    override fun <T : Any> contains(descriptor: OptionDescriptor<T>): Boolean {
        return entriesMap.containsKey(descriptor.key)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> getEntry(descriptor: OptionDescriptor<T>): OptionEntry<T>? {
        val registeredDescriptor = requireNotNull(OptionsManager.getOptionDescriptor(descriptor.key)) {
            "Descriptor for ${descriptor.key} not registered"
        } as OptionDescriptor<T>
        require(registeredDescriptor == descriptor) {
            "Descriptor for ${descriptor.key} was registered, but not equal to given one"
        }
        return if (contains(registeredDescriptor)) {
            entriesMap[registeredDescriptor.key] as? OptionEntry<T>
        } else if (registeredDescriptor.defaultValue != null) {
            OptionEntryImpl(registeredDescriptor, registeredDescriptor.defaultValue!!)
        } else {
            null
        }
    }

    override fun <T : Any> setEntry(descriptor: OptionDescriptor<T>, value: T) {
        if (descriptor.type == EntryValueType.OBJECT) {
            val objClass = checkNotNull(descriptor.objectClass) { "Object class cannot be null: ${descriptor.key}" }
            require(objClass.isInstance(value)) { "Value must be a instance of ${objClass.name}" }
        }
        descriptor.limitation?.let {
            require(it.match(value)) { "Value doesn't match limitation: $value" }
        }
        entriesMap[descriptor.key] = OptionEntryImpl(descriptor, value)
    }

    override fun removeEntry(descriptor: OptionDescriptor<*>) {
        entriesMap.remove(descriptor.key)
    }

    override suspend fun reload() {
        val model = checkNotNull(repo.findById(player)) { "Options reloading failed: Cannot fetch model" }
        entriesMap.clear()
        model.entries.forEach {
            createEntryFromModel(it)?.also { entry -> entriesMap[it.key] = entry }
        }
    }

    override suspend fun save() {
        repo.saveOrUpdate(this.toModel())
        notifier.notify(player)
    }
}