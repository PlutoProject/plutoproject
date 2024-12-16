package ink.pmc.menu.factories

import ink.pmc.menu.api.descriptor.PageDescriptor
import ink.pmc.menu.api.factory.PageDescriptorFactory
import ink.pmc.menu.descriptors.PageDescriptorImpl
import net.kyori.adventure.text.Component
import org.bukkit.Material

class PageDescriptorFactoryImpl : PageDescriptorFactory {
    override fun create(
        id: String,
        icon: Material,
        name: Component,
        description: List<Component>,
        customPagingButtonId: String?
    ): PageDescriptor {
        return PageDescriptorImpl(
            id, icon, name, description, customPagingButtonId
        )
    }
}