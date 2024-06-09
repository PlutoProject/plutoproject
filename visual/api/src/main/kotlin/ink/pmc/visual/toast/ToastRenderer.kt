package ink.pmc.visual.toast

import ink.pmc.visual.Renderer

@Suppress("UNUSED")
abstract class ToastRenderer<P> : Renderer<P, Toast> {

    companion object {
        lateinit var defaultRenderer: ToastRenderer<*>
    }

    abstract override fun render(player: P, obj: Toast)

}