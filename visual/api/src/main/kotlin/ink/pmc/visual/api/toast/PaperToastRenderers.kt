package ink.pmc.visual.api.toast

import org.bukkit.entity.Player

@Suppress("FunctionName")
fun NmsToastRenderer(): ToastRenderer<Player> {
    return PaperToastRenderers.nmsRenderer
}

@Suppress("FunctionName")
fun UltimateToastRenderer(): ToastRenderer<Player> {
    return PaperToastRenderers.ultimateToastRenderer
}

private object PaperToastRenderers {

    lateinit var nmsRenderer: ToastRenderer<Player>
    lateinit var ultimateToastRenderer: ToastRenderer<Player>

}