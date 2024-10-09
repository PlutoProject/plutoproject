package ink.pmc.options

import ink.pmc.options.api.EntryValueType.*
import ink.pmc.options.api.OptionDescriptor
import ink.pmc.options.api.OptionEntry
import ink.pmc.options.api.OptionsContainer
import ink.pmc.options.api.OptionsManager
import ink.pmc.options.models.OptionEntryModel
import ink.pmc.options.repositories.OptionsContainerRepository
import ink.pmc.utils.json.toObject
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializerOrNull
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@OptIn(InternalSerializationApi::class)
@Suppress("UNCHECKED_CAST")
internal fun createEntryFromModel(model: OptionEntryModel): OptionEntry<*>? {
    val descriptor = OptionsManager.getOptionDescriptor(model.key)
    if (descriptor == null) {
        // logger.warning("Descriptor not found for ${model.key}")
        return null
    }
    return when (descriptor.type) {
        INT -> OptionEntryImpl(descriptor as OptionDescriptor<Int>, Json.decodeFromString(model.value))
        LONG -> OptionEntryImpl(descriptor as OptionDescriptor<Long>, Json.decodeFromString(model.value))
        SHORT -> OptionEntryImpl(descriptor as OptionDescriptor<Short>, Json.decodeFromString(model.value))
        BYTE -> OptionEntryImpl(descriptor as OptionDescriptor<Byte>, Json.decodeFromString(model.value))
        DOUBLE -> OptionEntryImpl(descriptor as OptionDescriptor<Double>, Json.decodeFromString(model.value))
        FLOAT -> OptionEntryImpl(descriptor as OptionDescriptor<Float>, Json.decodeFromString(model.value))
        BOOLEAN -> OptionEntryImpl(descriptor as OptionDescriptor<Boolean>, Json.decodeFromString(model.value))
        STRING -> OptionEntryImpl(descriptor as OptionDescriptor<String>, Json.decodeFromString(model.value))
        OBJECT -> {
            val objClass =
                checkNotNull(descriptor.objectClass) { "Object class cannot be null: ${descriptor.key}" }
            val kSerializer = objClass.kotlin.serializerOrNull()
            if (kSerializer != null) {
                OptionEntryImpl(
                    descriptor as OptionDescriptor<Any>,
                    Json.decodeFromString(kSerializer, model.value)
                )
            } else {
                OptionEntryImpl(descriptor as OptionDescriptor<Any>, model.value.toObject(objClass))
            }
        }
    }
}

class OptionsManagerImpl : OptionsManager, KoinComponent {
    private val repo by inject<OptionsContainerRepository>()
    private val registeredDescriptors = mutableMapOf<String, OptionDescriptor<*>>()
    private val loadedContainersMap = ConcurrentHashMap<UUID, OptionsContainer>()
    override val loadedContainers
        get() = loadedContainersMap.values.toList()

    override fun isContainerLoaded(uuid: UUID): Boolean {
        return loadedContainersMap.containsKey(uuid)
    }

    override fun unloadContainer(uuid: UUID) {
        loadedContainersMap.remove(uuid)
    }

    override fun getLoadedContainer(uuid: UUID): OptionsContainer? {
        return loadedContainersMap[uuid]
    }

    private suspend fun fetchFromDatabase(uuid: UUID): OptionsContainer? {
        val model = repo.findById(uuid) ?: return null
        val entriesMap = mutableMapOf<String, OptionEntry<*>>()
        model.entries.forEach {
            createEntryFromModel(it)?.also { entry -> entriesMap[entry.descriptor.key] = entry }
        }
        return OptionsContainerImpl(uuid, entriesMap)
    }

    override suspend fun loadContainer(uuid: UUID): OptionsContainer? {
        return fetchFromDatabase(uuid)?.also { loadedContainersMap[uuid] = it }
    }

    override suspend fun createContainer(uuid: UUID): OptionsContainer {
        check(getContainer(uuid) == null) { "OptionContainer for $uuid already existed" }
        return OptionsContainerImpl(uuid, mutableMapOf()).also {
            save(it)
        }
    }

    /*
    * 非在线玩家的 OptionsContainer 不会被持久加载。
    * */
    override suspend fun getContainer(uuid: UUID): OptionsContainer? {
        return loadedContainersMap[uuid] ?: fetchFromDatabase(uuid)
    }

    override suspend fun getContainerOrCreate(uuid: UUID): OptionsContainer {
        return getContainer(uuid) ?: createContainer(uuid)
    }

    override suspend fun deleteContainer(uuid: UUID) {
        unloadContainer(uuid)
        repo.deleteById(uuid)
    }

    override suspend fun save(container: OptionsContainer) {
        container.save()
    }

    override suspend fun save(uuid: UUID) {
        loadedContainersMap[uuid]?.save()
    }

    override fun registerOptionDescriptor(descriptor: OptionDescriptor<*>) {
        require(!registeredDescriptors.containsKey(descriptor.key)) { "Descriptor for ${descriptor.key} already registered" }
        registeredDescriptors[descriptor.key] = descriptor
    }

    override fun getOptionDescriptor(key: String): OptionDescriptor<*>? {
        return registeredDescriptors[key]
    }
}