package ink.pmc.options

import ink.pmc.options.api.OptionsContainer
import ink.pmc.options.api.OptionDescriptor
import ink.pmc.options.api.OptionsManager
import ink.pmc.options.repositories.OptionsContainerRepository
import ink.pmc.utils.multiplaform.player.PlayerWrapper
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

class OptionsManagerImpl : OptionsManager, KoinComponent {
    private val repo by inject<OptionsContainerRepository>()
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

    override suspend fun save(container: OptionsContainer) {
        TODO("Not yet implemented")
    }

    override fun registerOptionDescriptor(descriptor: OptionDescriptor<*>) {
        require(!registeredDescriptors.containsKey(descriptor.key)) { "Descriptor for ${descriptor.key} already registered" }
        registeredDescriptors[descriptor.key] = descriptor
    }
}