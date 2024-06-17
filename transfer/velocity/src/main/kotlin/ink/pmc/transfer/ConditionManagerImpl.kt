package ink.pmc.transfer

import com.velocitypowered.api.proxy.Player
import ink.pmc.transfer.api.Destination
import ink.pmc.utils.multiplaform.player.velocity.wrapped

class ConditionManagerImpl(private val service: AbstractProxyTransferService) : AbstractConditionManager() {

    override val checkers: MutableMap<String, ConditionChecker> = mutableMapOf()

    override fun verifyCondition(player: Player, destination: Destination): Boolean {
        val id = destination.id

        if (!checkers.containsKey(id)) {
            return true
        }

        return checkers[id]!!(player.wrapped, id)
    }

}