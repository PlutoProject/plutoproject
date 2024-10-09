package ink.pmc.options.api

import ink.pmc.utils.inject.inlinedGet
import java.util.*

interface OptionsManager {
    companion object : OptionsManager by inlinedGet()

    val loadedContainers: List<OptionsContainer>

    fun isContainerLoaded(uuid: UUID): Boolean

    fun unloadContainer(uuid: UUID)

    fun getLoadedContainer(uuid: UUID): OptionsContainer?

    suspend fun loadContainer(uuid: UUID): OptionsContainer?

    suspend fun createContainer(uuid: UUID): OptionsContainer

    suspend fun getContainer(uuid: UUID): OptionsContainer?

    suspend fun getContainerOrCreate(uuid: UUID): OptionsContainer

    suspend fun deleteContainer(uuid: UUID)

    suspend fun save(container: OptionsContainer)

    suspend fun save(uuid: UUID)

    fun registerOptionDescriptor(descriptor: OptionDescriptor<*>)

    fun getOptionDescriptor(key: String): OptionDescriptor<*>?
}