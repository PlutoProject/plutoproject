package ink.pmc.common.utils.bedrock

import com.velocitypowered.api.proxy.Player

@Suppress("UNUSED")
val Player.isBedrock: Boolean
    get() = isBedrockSession(this.uniqueId)