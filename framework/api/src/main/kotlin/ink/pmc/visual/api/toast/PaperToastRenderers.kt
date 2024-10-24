package ink.pmc.visual.api.toast

import ink.pmc.framework.utils.inject.inlinedGet
import org.bukkit.entity.Player
import org.koin.core.qualifier.named

object BukkitDefaultToastRenderer: ToastRenderer<Player> by inlinedGet(named("internal"))

object BedrockToastRenderer: ToastRenderer<Player> by inlinedGet(named("bedrock"))