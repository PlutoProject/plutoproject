package ink.pmc.transfer

import com.velocitypowered.api.proxy.Player
import ink.pmc.transfer.api.ConditionManager
import ink.pmc.transfer.api.Destination

class ConditionManagerImpl(private val service: AbstractProxyTransferService) : ConditionManager {

    override fun verifyCondition(player: Player, destination: Destination): Boolean {
        TODO("Not yet implemented")
    }

}