package ink.pmc.menu

import ink.pmc.framework.interactive.ComposableFunction
import ink.pmc.menu.api.ButtonDescriptor
import ink.pmc.menu.api.Menu
import ink.pmc.menu.api.PageDescriptor

class MenuImpl : Menu {
    private val registeredPages = mutableListOf<PageDescriptor>()
    private val registeredButtons = mutableMapOf<ButtonDescriptor, ComposableFunction>()

    override fun registerPage(descriptor: PageDescriptor) {
        require(!registeredPages.any { it.id == descriptor.id }) { "PageDescriptor for ${descriptor.id} already registered" }
        registeredPages.add(descriptor)
    }

    override fun registerButton(descriptor: ButtonDescriptor, button: ComposableFunction) {
        require(!registeredButtons.keys.any { it.id == descriptor.id }) { "ButtonDescriptor for ${descriptor.id} already registered" }
        registeredButtons[descriptor] = button
    }

    override fun getPageDescriptor(id: String): PageDescriptor? {
        return registeredPages.firstOrNull { it.id == id }
    }

    override fun getButtonDescriptor(id: String): ButtonDescriptor? {
        return registeredButtons.keys.firstOrNull { it.id == id }
    }

    override fun getButton(descriptor: ButtonDescriptor): ComposableFunction? {
        return registeredButtons[descriptor]
    }
}