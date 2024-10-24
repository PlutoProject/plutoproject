package ink.pmc.transfer.scripting

import ink.pmc.advkt.component.RootComponentKt
import ink.pmc.framework.utils.structure.Builder
import net.kyori.adventure.text.Component

class ConditionDsl(private val destination: String): Builder<Condition> {

    var errorMessage: Component? = null
    private var checker: ConditionChecker = { true }

    fun errorMessage(component: RootComponentKt.() -> Unit) {
        errorMessage = RootComponentKt().apply(component).build()
    }

    fun checker(block: ConditionChecker) {
        checker = block
    }

    override fun build(): Condition {
        return Condition(destination, checker, errorMessage)
    }

}