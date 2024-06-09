package ink.pmc.visual.api.toast

import ink.pmc.visual.api.Renderer

@Suppress("UNUSED")
abstract class ToastRenderer<P> : Renderer<P, Toast> {

    companion object {
        lateinit var defaultRenderer: ToastRenderer<*>
    }

    abstract override fun render(player: P, obj: Toast)

}