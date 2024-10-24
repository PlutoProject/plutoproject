package ink.pmc.visual.api.toast

import ink.pmc.visual.api.GenericRenderer

@Suppress("UNUSED")
interface ToastRenderer<P> : GenericRenderer<P, Toast> {

    override fun render(player: P, obj: Toast)

}