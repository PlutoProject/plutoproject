package ink.pmc.menu.api

import ink.pmc.framework.utils.inject.inlinedGet
import ink.pmc.menu.api.descriptor.ButtonDescriptor
import ink.pmc.menu.api.descriptor.PageDescriptor

interface MenuService {
    companion object : MenuService by inlinedGet()

    val pages: List<PageDescriptor>

    fun registerPage(descriptor: PageDescriptor)

    fun registerButton(descriptor: ButtonDescriptor, button: MenuScopedComposableFunction)

    fun getPageDescriptor(id: String): PageDescriptor?

    fun getButtonDescriptor(id: String): ButtonDescriptor?

    fun getButton(descriptor: ButtonDescriptor): MenuScopedComposableFunction?
}