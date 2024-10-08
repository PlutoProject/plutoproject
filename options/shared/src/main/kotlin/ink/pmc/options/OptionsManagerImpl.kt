package ink.pmc.options

import ink.pmc.options.api.OptionsContainer
import ink.pmc.options.api.OptionDescriptor
import ink.pmc.options.api.OptionsManager
import ink.pmc.utils.multiplaform.player.PlayerWrapper
import java.util.*

class OptionsManagerImpl : OptionsManager {
    private val registeredDescriptors = mutableMapOf<String, OptionDescriptor<*>>()

    override suspend fun getContainer(uuid: UUID): OptionsContainer? {
        TODO("Not yet implemented")
    }

    override suspend fun getContainer(player: PlayerWrapper<*>): OptionsContainer? {
        TODO("Not yet implemented")
    }

    override suspend fun getContainerOrCreate(uuid: UUID): OptionsContainer {
        TODO("Not yet implemented")
    }

    override suspend fun getContainerOrCreate(player: PlayerWrapper<*>): OptionsContainer {
        TODO("Not yet implemented")
    }

    override fun registerOptionDescriptor(descriptor: OptionDescriptor<*>) {
        require(!registeredDescriptors.containsKey(descriptor.key)) { "Descriptor for ${descriptor.key} already registered" }
        registeredDescriptors[descriptor.key] = descriptor
    }
}