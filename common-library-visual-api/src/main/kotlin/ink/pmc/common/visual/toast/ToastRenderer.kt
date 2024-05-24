package ink.pmc.common.visual.toast

import ink.pmc.common.visual.Renderer

@Suppress("UNUSED")
abstract class ToastRenderer<P> : Renderer<P, Toast> {

    abstract override fun render(player: P, obj: Toast)

}