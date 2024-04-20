package ink.pmc.common.utils.bedrock

import org.bukkit.entity.Player

@Suppress("UNUSED")
val Player.isBedrock: Boolean
    get() = isBedrockSession(this.uniqueId)