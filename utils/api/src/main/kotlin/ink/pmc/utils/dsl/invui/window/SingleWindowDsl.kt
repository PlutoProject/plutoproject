package ink.pmc.utils.dsl.invui.window

import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper
import xyz.xenondevs.invui.window.AbstractSingleWindow
import xyz.xenondevs.invui.window.Window

class SingleWindowDsl : WindowDsl<AbstractSingleWindow>() {

    override fun build(): AbstractSingleWindow {
        return Window.single()
            .setTitle(AdventureComponentWrapper(title))
            .setViewer(viewer!!)
            .setGui(gui!!)
            .apply {
                openHandlers.forEach { handler -> addOpenHandler { handler(viewer!!) } }
                closeHandlers.forEach { handler -> addCloseHandler { handler(viewer!!) } }
                outsideClickHandlers.forEach { handler -> addOutsideClickHandler { handler(viewer!!, it) } }
            }
            .build()
            .apply {
                window = this
                whenBuild(window)
            } as AbstractSingleWindow
    }

}

inline fun singleWindow(window: SingleWindowDsl.() -> Unit): Window {
    return SingleWindowDsl().apply(window).build()
}