package ink.pmc.transfer.proxy

import ink.pmc.transfer.api.ConditionManager
import ink.pmc.transfer.api.Destination
import ink.pmc.transfer.scripting.Condition
import ink.pmc.utils.multiplaform.player.PlayerWrapper
import net.kyori.adventure.text.Component

class ProxyConditionManager(private val conditions: Collection<Condition>) : ConditionManager {

    override suspend fun verifyCondition(player: PlayerWrapper<*>, destination: Destination): Pair<Boolean, Component?> {
        println("Conditions: $conditions")
        val id = destination.id
        val condition = conditions.firstOrNull { it.destination == id }

        if (condition == null) {
            return true to null
        }

        return condition.checker(player) to condition.errorMessage
    }

}