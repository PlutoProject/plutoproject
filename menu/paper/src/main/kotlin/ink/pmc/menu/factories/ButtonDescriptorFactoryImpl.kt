package ink.pmc.menu.factories

import ink.pmc.menu.api.descriptor.ButtonDescriptor
import ink.pmc.menu.api.factory.ButtonDescriptorFactory
import ink.pmc.menu.descriptors.ButtonDescriptorImpl

class ButtonDescriptorFactoryImpl : ButtonDescriptorFactory {
    override fun create(id: String): ButtonDescriptor {
        return ButtonDescriptorImpl(id)
    }
}