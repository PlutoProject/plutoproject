package ink.pmc.transfer.proxy

import ink.pmc.transfer.api.ConditionManager
import ink.pmc.transfer.api.Destination
import ink.pmc.transfer.scripting.Condition
import ink.pmc.utils.multiplaform.player.PlayerWrapper

class ProxyConditionManager(private val conditions: Collection<Condition>) : ConditionManager {

    override suspend fun verifyCondition(player: PlayerWrapper<*>, destination: Destination): Boolean {
        val id = destination.id
        val condition = conditions.firstOrNull { it.destination == id }

        if (condition == null) {
            return true
        }

        return condition.checker(player)
    }

}