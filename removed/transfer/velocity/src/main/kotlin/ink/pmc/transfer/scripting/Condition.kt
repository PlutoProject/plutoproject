package ink.pmc.transfer.scripting

import ink.pmc.framework.utils.multiplaform.player.PlayerWrapper
import net.kyori.adventure.text.Component

typealias ConditionChecker = (player: PlayerWrapper<*>) -> Boolean

data class Condition(
    val destination: String,
    val checker: ConditionChecker,
    val errorMessage: Component?
)