package ink.pmc.transfer.proxy

import ink.pmc.transfer.api.ConditionManager
import ink.pmc.transfer.api.Destination
import ink.pmc.utils.multiplaform.player.PlayerWrapper

typealias ConditionChecker = suspend (player: PlayerWrapper<*>, destination: String) -> Boolean

class ProxyConditionManager(private val service: AbstractProxyTransferService) : ConditionManager {

    private val checkers: MutableMap<String, ConditionChecker> = mutableMapOf()

    override suspend fun verifyCondition(player: PlayerWrapper<*>, destination: Destination): Boolean {
        val id = destination.id

        if (!checkers.containsKey(id)) {
            return true
        }

        return checkers[id]!!(player, id)
    }

}