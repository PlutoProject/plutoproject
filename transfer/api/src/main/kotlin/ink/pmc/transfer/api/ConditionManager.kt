package ink.pmc.transfer.api

import ink.pmc.utils.multiplaform.player.PlayerWrapper

interface ConditionManager {

    suspend fun verifyCondition(player: PlayerWrapper<*>, destination: Destination): Boolean

}