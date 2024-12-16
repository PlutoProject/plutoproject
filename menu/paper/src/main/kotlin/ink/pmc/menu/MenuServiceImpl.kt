package ink.pmc.menu

import ink.pmc.menu.api.ButtonDescriptor
import ink.pmc.menu.api.MenuScopedComposableFunction
import ink.pmc.menu.api.MenuService
import ink.pmc.menu.api.PageDescriptor

class MenuServiceImpl : MenuService {
    private val registeredPages = mutableListOf<PageDescriptor>()
    private val registeredButtons = mutableMapOf<ButtonDescriptor, MenuScopedComposableFunction>()
    override val pages: List<PageDescriptor>
        get() = registeredPages

    override fun registerPage(descriptor: PageDescriptor) {
        require(!registeredPages.any { it.id == descriptor.id }) { "PageDescriptor for ${descriptor.id} already registered" }
        registeredPages.add(descriptor)
    }

    override fun registerButton(descriptor: ButtonDescriptor, button: MenuScopedComposableFunction) {
        require(!registeredButtons.keys.any { it.id == descriptor.id }) { "ButtonDescriptor for ${descriptor.id} already registered" }
        registeredButtons[descriptor] = button
    }

    override fun getPageDescriptor(id: String): PageDescriptor? {
        return registeredPages.firstOrNull { it.id == id }
    }

    override fun getButtonDescriptor(id: String): ButtonDescriptor? {
        return registeredButtons.keys.firstOrNull { it.id == id }
    }

    override fun getButton(descriptor: ButtonDescriptor): MenuScopedComposableFunction? {
        return registeredButtons[descriptor]
    }
}