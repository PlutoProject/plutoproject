package ink.pmc.transfer.api

import ink.pmc.framework.utils.multiplaform.player.PlayerWrapper
import net.kyori.adventure.text.Component

interface ConditionManager {

    suspend fun verifyCondition(player: PlayerWrapper<*>, destination: Destination): Pair<Boolean, Component?>

}