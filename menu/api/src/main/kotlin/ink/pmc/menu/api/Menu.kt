package ink.pmc.menu.api

import ink.pmc.framework.interactive.ComposableFunction
import ink.pmc.framework.utils.inject.inlinedGet

interface Menu {
    companion object : Menu by inlinedGet()

    fun registerPage(descriptor: PageDescriptor)

    fun registerButton(descriptor: ButtonDescriptor, button: ComposableFunction)

    fun getPageDescriptor(id: String): PageDescriptor?

    fun getButtonDescriptor(id: String): ButtonDescriptor?

    fun getButton(descriptor: ButtonDescriptor): ComposableFunction?
}