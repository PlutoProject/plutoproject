package ink.pmc.menu.api

import net.kyori.adventure.text.Component

interface PageDescriptor {
    val id: String
    val name: Component
    val description: List<Component>
    val customPagingButtonId: String?
}