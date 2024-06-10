package ink.pmc.visual.api.toast

import org.bukkit.entity.Player

@Suppress("FunctionName", "UNUSED")
fun NmsToastRenderer(): ToastRenderer<Player> {
    return PaperToastRenderers.nmsRenderer
}

object PaperToastRenderers {

    lateinit var nmsRenderer: ToastRenderer<Player>

}