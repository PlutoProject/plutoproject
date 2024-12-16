package ink.pmc.menu

import ink.pmc.menu.api.PageDescriptor
import net.kyori.adventure.text.Component

data class PageDescriptorImpl(
    override val id: String,
    override val name: Component,
    override val description: List<Component>,
    override val customPagingButtonId: String?
) : PageDescriptor