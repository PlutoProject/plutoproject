package ink.pmc.transfer.api

import ink.pmc.utils.multiplaform.player.PlayerWrapper
import net.kyori.adventure.text.Component

interface ConditionManager {

    suspend fun verifyCondition(player: PlayerWrapper<*>, destination: Destination): Pair<Boolean, Component?>

}