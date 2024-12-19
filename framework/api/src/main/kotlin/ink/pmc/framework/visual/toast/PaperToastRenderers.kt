package ink.pmc.framework.visual.toast

import ink.pmc.framework.inject.inlinedGet
import org.bukkit.entity.Player
import org.koin.core.qualifier.named

object BukkitDefaultToastRenderer :
    ToastRenderer<Player> by inlinedGet(named("internal"))