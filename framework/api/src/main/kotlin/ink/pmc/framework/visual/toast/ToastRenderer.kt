package ink.pmc.framework.visual.toast

import ink.pmc.framework.visual.GenericRenderer

@Suppress("UNUSED")
interface ToastRenderer<P> : GenericRenderer<P, Toast> {

    override fun render(player: P, obj: Toast)

}