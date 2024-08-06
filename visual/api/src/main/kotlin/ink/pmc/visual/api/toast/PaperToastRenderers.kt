package ink.pmc.visual.api.toast

import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

object BukkitDefaultToastRenderer: ToastRenderer<Player> by object : KoinComponent {
    val instance by inject<ToastRenderer<Player>>(named("nms"))
}.instance