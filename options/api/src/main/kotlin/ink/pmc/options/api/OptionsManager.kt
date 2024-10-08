package ink.pmc.options.api

import ink.pmc.utils.inject.inlinedGet
import ink.pmc.utils.multiplaform.player.PlayerWrapper
import java.util.UUID

interface OptionsManager {
    companion object : OptionsManager by inlinedGet()

    val loadedContainers: List<OptionsContainer>

    fun isContainerLoaded(uuid: UUID): Boolean

    fun unloadContainer(uuid: UUID)

    fun getLoadedContainer(uuid: UUID): OptionsContainer?

    suspend fun createContainer(uuid: UUID): OptionsContainer

    suspend fun createContainer(player: PlayerWrapper<*>): OptionsContainer

    suspend fun getContainer(uuid: UUID): OptionsContainer?

    suspend fun getContainer(player: PlayerWrapper<*>): OptionsContainer?

    suspend fun getContainerOrCreate(uuid: UUID): OptionsContainer

    suspend fun getContainerOrCreate(player: PlayerWrapper<*>): OptionsContainer

    suspend fun deleteContainer(uuid: UUID)

    suspend fun deleteContainer(player: PlayerWrapper<*>)

    suspend fun save(container: OptionsContainer)

    suspend fun save(uuid: UUID)

    suspend fun save(player: PlayerWrapper<*>)

    fun registerOptionDescriptor(descriptor: OptionDescriptor<*>)

    fun getOptionDescriptor(key: String): OptionDescriptor<*>?
}