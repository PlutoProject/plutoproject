package ink.pmc.transfer.api

import com.velocitypowered.api.proxy.Player

interface ConditionManager {

    fun verifyCondition(player: Player, destination: Destination): Boolean

}