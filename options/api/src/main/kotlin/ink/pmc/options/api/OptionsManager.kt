package ink.pmc.options.api

import ink.pmc.utils.inject.inlinedGet
import java.util.*

interface OptionsManager {
    companion object : OptionsManager by inlinedGet()

    val loadedPlayers: List<PlayerOptions>

    fun isPlayerLoaded(uuid: UUID): Boolean

    fun unloadPlayer(uuid: UUID)

    fun getLoadedOptions(uuid: UUID): PlayerOptions?

    suspend fun loadOptions(uuid: UUID): PlayerOptions?

    suspend fun createOptions(uuid: UUID): PlayerOptions

    suspend fun getOptions(uuid: UUID): PlayerOptions?

    suspend fun getOptionsOrCreate(uuid: UUID): PlayerOptions

    suspend fun deleteOptions(uuid: UUID)

    suspend fun save(options: PlayerOptions)

    suspend fun save(uuid: UUID)

    fun registerOptionDescriptor(descriptor: OptionDescriptor<*>)

    fun getOptionDescriptor(key: String): OptionDescriptor<*>?
}