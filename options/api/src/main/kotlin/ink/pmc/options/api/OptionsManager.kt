package ink.pmc.options.api

import ink.pmc.utils.inject.inlinedGet
import ink.pmc.utils.multiplaform.player.PlayerWrapper
import java.util.UUID

interface OptionsManager {
    companion object : OptionsManager by inlinedGet()

    suspend fun getContainer(uuid: UUID): OptionsContainer?

    suspend fun getContainer(player: PlayerWrapper<*>): OptionsContainer?

    suspend fun getContainerOrCreate(uuid: UUID): OptionsContainer

    suspend fun getContainerOrCreate(player: PlayerWrapper<*>): OptionsContainer

    fun registerOptionDescriptor(descriptor: OptionDescriptor<*>)
}