package ink.pmc.transfer

import ink.pmc.transfer.api.ConditionManager
import ink.pmc.utils.multiplaform.player.PlayerWrapper

typealias ConditionChecker = (player: PlayerWrapper<*>, destination: String) -> Boolean

abstract class AbstractConditionManager : ConditionManager {

    abstract val checkers: MutableMap<String, ConditionChecker>

}